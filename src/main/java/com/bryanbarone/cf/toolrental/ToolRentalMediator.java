package com.bryanbarone.cf.toolrental;

import com.bryanbarone.cf.toolrental.common.ToolRentalValidationException;
import com.bryanbarone.cf.toolrental.model.Agreement;
import com.bryanbarone.cf.toolrental.model.Order;
import com.bryanbarone.cf.toolrental.model.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;


public class ToolRentalMediator {

    private final static DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("M/d/uu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Processes the agreement for a given tool rental order.
     * @param order - the order form inputs for a tool rental
     * @return Agreement object representing the details of the order and rental agreement
     * @throws ToolRentalValidationException throws validation exception
     */
    public Agreement processOrder(Order order, Tool tool) throws ToolRentalValidationException {

        // validate order input and set fields on agreement
        Agreement agreement = validateOrder(order);

        // set tool details and use cost data from db for calculations
        BigDecimal dayCharge = tool.getCosts().getCharge();
        agreement.setToolCode(tool.getCode());
        agreement.setToolType(tool.getType());
        agreement.setToolBrand(tool.getBrand());
        agreement.setRentalCharge(dayCharge);

        int rentalDays = agreement.getRentalDays();
        int discountPercent = agreement.getDiscount();
        // parse date for logic calculations - double check ensure valid date provided
        try {
            LocalDate checkoutDate = LocalDate.parse(order.getCheckoutDate(), DATE_FORMATER);
            // based on requirements, chargeable days are day after checkout through and including due date
            LocalDate returnDate = checkoutDate.plusDays(rentalDays);
            agreement.setReturnDate(returnDate.format(DATE_FORMATER));
            agreement.setCheckoutDate(order.getCheckoutDate());

            // determine if tool has weekend or holiday charges
            int h_days = 0; int we_days = 0;
            if (!tool.getCosts().isHoliday()) { // if no holiday charge, find holiday days
                h_days = getLaborDayCount(checkoutDate, returnDate) + getIndependenceDayCount(checkoutDate, returnDate);
            }
            if (!tool.getCosts().isWeekend()) { // if no weekend charge, find weekend days
                we_days = getWeekendDayCount(checkoutDate, returnDate, rentalDays);
            }
            int chargeDays = rentalDays - h_days - we_days;
            agreement.setChargeDays(chargeDays);
            // calculate discount and final charge, round half up
            BigDecimal preDiscount = dayCharge.multiply(new BigDecimal(chargeDays)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal discount = preDiscount.multiply(new BigDecimal(discountPercent/100.0)).setScale(2, RoundingMode.HALF_UP);
            agreement.setPreDiscountAmount(preDiscount);
            agreement.setDiscountAmount(discount);
            agreement.setFinalAmount(preDiscount.subtract(discount));

        } catch (DateTimeParseException e) {
            // e.printStackTrace();
            throw new ToolRentalValidationException("Please provide a valid checkout date", "checkoutDate");
        }
        // System.out.println(agreement.toString());

        // return receipt/agreement
        return agreement;
    }

    /**
     * Method to determine the number of weekend days there are within the timeframe of
     * beginDate, endDate, and the provided daycount. Checkout day is not included in count.
     * @param beginDate - the start of the date range
     * @param endDate - the end of the date range
     * @param dayCount - number of days in range
     * @return int number of Labor Day holidays
     */
    public static int getWeekendDayCount(LocalDate beginDate, LocalDate endDate, int dayCount) {
        beginDate = beginDate.plusDays(1); // checkout date is not included in chargeable days
        // find weekday days and subtract
        DayOfWeek beginDOW = beginDate.getDayOfWeek();
        DayOfWeek endDOW = endDate.getDayOfWeek();
        int count = dayCount - 2 * (dayCount/7); // remove the weekends

        if (dayCount % 7 != 0) { // if not even week, handle outliers
            if (beginDOW == DayOfWeek.SUNDAY) {
                count -= 1;
            } else if (endDOW == DayOfWeek.SUNDAY) { // check if end is Sunday
                count -= 1;
            } else if (endDOW.getValue() < beginDOW.getValue()) { // additional weekend to calculate
                count -= 2;
            }
        }
        // return the weekend
        return dayCount - count;
    }


    /* Making decision rental period is less then a year (365 days), so only need to consider 1 holiday instance
     * Since requirements are being nice, we do not need to determine if holidays
     * fall on a weekend (they are still considered on the prior or next weekday)
     * Thank you!
     */

    /**
     * Method to determine the number of Labor Day 'days' within the timeframe of
     * beginDate and endDate.
     * @param beginDate - the start of the date range
     * @param endDate - the end of the date range
     * @return int number of Labor Day holidays
     */
    public static int getLaborDayCount(LocalDate beginDate, LocalDate endDate) {
        LocalDate LABORDAY;
        int year = 0; // determine which year to use for holiday to check range
        if (beginDate.getMonth().getValue() <= Month.SEPTEMBER.getValue()) year = beginDate.getYear();
        else if (endDate.getMonth().getValue() >= Month.SEPTEMBER.getValue()) year = endDate.getYear();
        if (year != 0) {
            // get first Monday in September for year holiday may fall in range
            LABORDAY = LocalDate.of(year, 9, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
            // check if in range
            if (LABORDAY.isAfter(beginDate) && LABORDAY.isBefore(endDate.plusDays(1))) {
                return 1;
            }
        }
        // outside of range, no holiday in timeframe
        return 0;
    }

    /**
     * Method to determine the number of Independence Day 'days' within the timeframe of
     * beginDate and endDate.
     * ** If July 4th falls on Sunday, make sure next day (Monday is in the timeframe), if falls on Saturday
     * then make sure previous day (Friday is in timeframe), otherwise do not count
     * @param beginDate - the start of the date range
     * @param endDate - the end of the date range
     * @return int number of Independence Day holidays
     */
    public static int getIndependenceDayCount(LocalDate beginDate, LocalDate endDate) {
        LocalDate JULY4;
        int year = 0; // determine which year to use for holiday to check range
        if (beginDate.getMonth().getValue() <= Month.JULY.getValue()) year = beginDate.getYear();
        else if (endDate.getMonth().getValue() >= Month.JULY.getValue()) year = endDate.getYear();
        if (year != 0) {
            JULY4 = LocalDate.of(year, 7, 4);
            if (JULY4.isAfter(beginDate) && JULY4.isBefore(endDate.plusDays(1))) {
                // in range, if Sunday then make sure Monday (next day is in range)
                if (JULY4.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    if (JULY4.plusDays(1).isBefore(endDate.plusDays(1))) return 1;
                } else if (JULY4.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    if (JULY4.minusDays(1).isAfter(beginDate)) return 1;
                } else return 1;
            }
        }
        // outside of range, no holiday in timeframe
        return 0;
    }

    /**
     * Method to validate the rental order input values and respond with appropriate validation error.
     * @param order - input for rental order
     * @return Agreement with order details populated
     * @throws ToolRentalValidationException throws validation exception
     */
    private Agreement validateOrder(Order order) throws ToolRentalValidationException {
        Agreement agreement = new Agreement();

        // validate rental days
        try {
            int rentalDays = Integer.parseInt(order.getRentalDays());
            if (rentalDays < 1) throw new NumberFormatException();
            if (rentalDays > 365) throw new ToolRentalValidationException("1 year rental limit, please consider purchasing item", "rentalDays");
            agreement.setRentalDays(rentalDays);

        } catch (NumberFormatException nfe) {
            throw new ToolRentalValidationException("Please enter a valid number of rental days", "rentalDays");
        }

        // validate discount percentage
        try {
            int discountPercent = Integer.parseInt(order.getDiscount());
            if (discountPercent < 0 || discountPercent > 100) throw new NumberFormatException();
            agreement.setDiscount(discountPercent);

        } catch (NumberFormatException nfe) {
            throw new ToolRentalValidationException("Please enter a valid discount percent", "discount");
        }

        return agreement;
    }

}
