package com.bryanbarone.cf.toolrental;

import com.bryanbarone.cf.toolrental.common.ToolRentalValidationException;
import com.bryanbarone.cf.toolrental.model.Agreement;
import com.bryanbarone.cf.toolrental.model.Order;
import com.bryanbarone.cf.toolrental.model.Tool;
import com.bryanbarone.cf.toolrental.repository.ToolJDBCRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ToolRentalMediatorTest {

    @Autowired
    private ToolJDBCRepository repository;
    private static ToolRentalMediator mediator;

    @BeforeAll
    static void setup() {
        mediator = new ToolRentalMediator();
    }

    // tests for process order
    @Test
    void processOrder_test1() throws ToolRentalValidationException {
        // test case 1 - 101% discount
        Tool tool = repository.findToolByCode("JAKR");
        Order order = new Order("JAKR", 5, 101, "9/3/15");
        try {
            mediator.processOrder(order, tool);
            Assertions.fail("Failure processing order with discount over 100 percent");
        } catch (ToolRentalValidationException ve) {
            // validation exception is the valid test result
        }
    }

    @Test
    void processOrder_test2() throws ToolRentalValidationException {
        // test case 2 - 10% discount on 3 day rental (weekend charge, no holiday charge
        Tool tool = repository.findToolByCode("LADW");
        Order order = new Order("LADW", 3, 10, "7/2/20");
        Agreement agreement = mediator.processOrder(order, tool);
        assertEquals(new BigDecimal("3.58"), agreement.getFinalAmount());
    }

    @Test
    void processOrder_test3() throws ToolRentalValidationException {
        // test case 3 - 25% discount on 5 day rental (holiday charge, no weekend charge)
        Tool tool = repository.findToolByCode("CHNS");
        Order order = new Order("CHNS", 5, 25, "7/2/15");
        Agreement agreement = mediator.processOrder(order, tool);
        assertEquals(new BigDecimal("3.35"), agreement.getFinalAmount());
        assertEquals(3, agreement.getChargeDays());
    }

    @Test
    void processOrder_test4() throws ToolRentalValidationException {
        // test case 4 - 0% discount on 6 day rental (no weekend charge, no holiday charge)
        Tool tool = repository.findToolByCode("JAKD");
        Order order = new Order("JAKD", 6, 0, "9/3/15");
        Agreement agreement = mediator.processOrder(order, tool);
        assertEquals(new BigDecimal("8.97"), agreement.getFinalAmount());
        assertEquals(3, agreement.getChargeDays());
    }

    @Test
    void processOrder_test5() throws ToolRentalValidationException {
        // test case 5 - 0% discount on 9 day rental (no weekend charge, no holiday charge)
        Tool tool = repository.findToolByCode("JAKD");
        Order order = new Order("JAKD", 9, 0, "7/2/15");
        Agreement agreement = mediator.processOrder(order, tool);
        assertEquals(new BigDecimal("17.94"), agreement.getFinalAmount());
        assertEquals(6, agreement.getChargeDays());
    }

    @Test
    void processOrder_test6() throws ToolRentalValidationException {
        // test case 6 - 50% discount on 4 day rental (no weekend charge, no holiday charge)
        Tool tool = repository.findToolByCode("JAKD");
        Order order = new Order("JAKD", 4, 50, "7/2/20");
        Agreement agreement = mediator.processOrder(order, tool);
        assertEquals(new BigDecimal("1.49"), agreement.getFinalAmount());
        assertEquals(1, agreement.getChargeDays());
    }


    @Test
    void getWeekendDayCount() { // TODO: break up date/holiday tests
        // check Monday through Friday
        int count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 10, 5), LocalDate.of(2020, 10, 9), 4);
        assertEquals(0, count);

        // check full week, Sunday to Sunday
        count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 10, 11), LocalDate.of(2020, 10, 18), 7);
        assertEquals(2, count);

        // check 3 week period, Friday to Friday
        count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 10, 9), LocalDate.of(2020, 10, 20), 21);
        assertEquals(6, count);

        // check across months, Sunday to Monday
        count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 9, 27), LocalDate.of(2020, 10, 5), 8);
        assertEquals(2, count);

        // check 1 day on weekend Saturday
        count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 10, 17), LocalDate.of(2020, 10, 18), 1);
        assertEquals(1, count);

        // check 1 day starting on Sunday
        count = ToolRentalMediator.getWeekendDayCount(LocalDate.of(2020, 10, 18), LocalDate.of(2020, 10, 19), 1);
        assertEquals(0, count);
    }

    @Test
    void getLaborDayCount() { // TODO: break up date/holiday tests
        // check case outside of range prior to labor day
        int count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 8, 4));
        assertEquals(0, count);

        // check case outside of range after labor day
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2020, 9, 14), LocalDate.of(2020, 10, 18));
        assertEquals(0, count);

        // check labor day in range
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2020, 9, 1), LocalDate.of(2020, 12, 1));
        assertEquals(1, count);

        // check case not in range landing on day before labor day
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2020, 9, 1), LocalDate.of(2020, 9, 6));
        assertEquals(0, count);

        // check case starting on labor day, start day is not inclusive of chargeable time
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2020, 9, 7), LocalDate.of(2020, 9, 14));
        assertEquals(0, count);

        // check case in range spanning different years
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2019, 12, 1), LocalDate.of(2020, 9, 11));
        assertEquals(1, count);

        // check case begin month is before, end month is before
        count = ToolRentalMediator.getLaborDayCount(LocalDate.of(2019, 8, 20), LocalDate.of(2020, 8, 1));
        assertEquals(1, count);
    }

    @Test
    void getIndependenceDayCount() { // TODO: break up date/holiday tests
        // check valid case when holiday is in timeframe
        int count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 8, 4));
        assertEquals(1, count);

        // check when rented on 7/4 and Saturday, should not count
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2020, 7, 4), LocalDate.of(2020, 8, 4));
        assertEquals(0, count);

        // check when rented on 7/3 and 7/4 is Saturday, should not count since holiday is observed on Friday and is not charged
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2020, 7, 3), LocalDate.of(2020, 8, 4));
        assertEquals(0, count);

        // check when rental ends on 7/4, should count
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 4));
        assertEquals(1, count);

        // check when rental ends on 7/4 and is Sunday, should not count since holiday is considered Monday
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2021, 7, 1), LocalDate.of(2021, 7, 4));
        assertEquals(0, count);

        // check inside range of multiple years
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2019, 12, 1), LocalDate.of(2020, 7, 7));
        assertEquals(1, count);

        // check outside range of multiple years
        count = ToolRentalMediator.getIndependenceDayCount(LocalDate.of(2019, 11, 1), LocalDate.of(2020, 7, 1));
        assertEquals(0, count);
    }

    @Test
    void getChargeableMinusWeekendHoliday_tiny() {
        // check 1 day weekday rental
        LocalDate bdate = LocalDate.of(2020, 9, 14);
        LocalDate edate = LocalDate.of(2020, 9, 15);
        int days = 1;
        int count = ToolRentalMediator.getIndependenceDayCount(bdate, edate) + ToolRentalMediator.getLaborDayCount(bdate, edate) +
                ToolRentalMediator.getWeekendDayCount(bdate, edate, days);
        assertEquals(1, days - count);
    }

    @Test
    void getChargeableMinusWeekendHoliday_small() {
        // check 2 week rental with 4 weekends and 1 holiday with no charge
        LocalDate bdate = LocalDate.of(2020, 8, 31);
        LocalDate edate = LocalDate.of(2020, 9, 14);
        int days = 14;
        int count = ToolRentalMediator.getIndependenceDayCount(bdate, edate) + ToolRentalMediator.getLaborDayCount(bdate, edate) +
                ToolRentalMediator.getWeekendDayCount(bdate, edate, days);
        assertEquals(9, days - count);
    }

    @Test
    void getChargeableMinusWeekendHoliday_medium() {
        // check 75 day rental and 1 holiday with no charge on holiday and weekends
        LocalDate bdate = LocalDate.of(2020, 5, 1);
        LocalDate edate = LocalDate.of(2020, 7, 15);
        int days = 75;
        int count = ToolRentalMediator.getIndependenceDayCount(bdate, edate) + ToolRentalMediator.getLaborDayCount(bdate, edate) +
                ToolRentalMediator.getWeekendDayCount(bdate, edate, days);
        assertEquals(52, days-count);
    }

    @Test
    void getChargeableMinusWeekendHoliday_large() {
        // check 275 day rental and 2 holiday with no charge on holiday and weekends
        LocalDate bdate = LocalDate.of(2019, 12, 30);
        LocalDate edate = LocalDate.of(2020, 9, 30);
        int days = 275;
        int count = ToolRentalMediator.getIndependenceDayCount(bdate, edate) + ToolRentalMediator.getLaborDayCount(bdate, edate) +
                ToolRentalMediator.getWeekendDayCount(bdate, edate, days);
        assertEquals(195, days-count);
    }

}