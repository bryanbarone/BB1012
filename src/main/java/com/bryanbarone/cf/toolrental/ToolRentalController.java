package com.bryanbarone.cf.toolrental;

import com.bryanbarone.cf.toolrental.common.ToolRentalValidationException;
import com.bryanbarone.cf.toolrental.model.AbstractResponse;
import com.bryanbarone.cf.toolrental.model.Agreement;
import com.bryanbarone.cf.toolrental.model.Order;
import com.bryanbarone.cf.toolrental.model.Tool;
import com.bryanbarone.cf.toolrental.repository.ToolJDBCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ToolRentalController {

    @Autowired
    private ToolJDBCRepository toolRepo;

    @GetMapping("/toollist")
    public List<Tool> getAllTools() {
        return toolRepo.findAllTools();
    }

    // For testing single get tool method through REST call / Postman
    // or for future use in UI
    @GetMapping("/tool/{code}")
    public AbstractResponse getTool(@PathVariable String code) throws Exception {
        AbstractResponse resp = new AbstractResponse();
        try {
            Tool tool = toolRepo.findToolByCode(code);
            resp.setResult(tool);

        } catch (EmptyResultDataAccessException er) {
            resp.setStatus(AbstractResponse.STATUS_ERROR);
            resp.setMessage("Invalid tool code " + code + ", item not found");
        }

        return resp;
    }

    @PostMapping("/checkout")
    public AbstractResponse rentalCheckout(@RequestBody Order userOrder) throws Exception {
        AbstractResponse resp = new AbstractResponse();

        // process the order - hand off to mediator for business logic
        try {
            Tool tool = toolRepo.findToolByCode(userOrder.getToolCode());

            ToolRentalMediator mediator = new ToolRentalMediator();
            Agreement agreement = mediator.processOrder(userOrder, tool);
            resp.setResult(agreement);

        } catch (ToolRentalValidationException te) {
            // handle validation errors, set in response for consumer / UI
            resp.setStatus(AbstractResponse.STATUS_VALIDATION);
            resp.setMessage(te.getMessage());
            resp.setField(te.getField());
        }

        return resp;
    }
}
