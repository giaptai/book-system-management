package com.hrm.books.controller;

import com.hrm.books.service.impl.AuthService;
import com.hrm.books.utilities.dto.address.ReqAddress;
import com.hrm.books.utilities.dto.visitor.ReqVisitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/my-account")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {
    final AuthService authService;


    /**
     * information, address, bill
     * <br/>
     * <a href="https://stackoverflow.com/questions/24398653/how-to-get-swagger-uis-parameter-to-be-dropdown-menu-instead-of-text-input">LOL</a>
     */
    @GetMapping(path = "/{action}")
    @Operation(
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "action",
                            schema = @Schema(allowableValues = {"info", "address", "bill"})
                    )
            })
    public ResponseEntity<?> authInfo(@PathVariable(name = "action") String actions,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "size", defaultValue = "15") int size) {
        return new ResponseEntity<>(authService.authGeneral(actions, null, null), HttpStatus.OK);
    }

    @GetMapping(path = "/{action}/{id}")
    @Operation(
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "action",
                            schema = @Schema(allowableValues = {"bill"})
                    ),
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id",
                            allowEmptyValue = true,
                            hidden = true
                    )
            })
    public ResponseEntity<?> authInfoWithId(@PathVariable(name = "action") String actions, @PathVariable(name = "id") String id) {
        return new ResponseEntity<>(authService.authGeneral(actions, id, null), HttpStatus.OK);
    }

    /**
     * info: password, email v.v
     */
    @PutMapping(path = "/{action}/{id}")
    @Operation(
            description = "<b>DESCRIPTION: Use for ADD or MODIFY</b>",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "action",
                            schema = @Schema(allowableValues = {"address", "password", "email"})
                    ),
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "id"
                    ),

            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(
                            anyOf = {ReqAddress.class, ReqVisitor.class}
                    ),
                            examples = {
                                    @ExampleObject(
                                            name = "reqAddressExample",
                                            value = "{\"nameReceive\": \"John Doe\", \"phoneReceive\": \"0989927812\", \"location\": \"123 Main St\"}"
                                    ),
                                    @ExampleObject(
                                            name = "reqVisitorExample",
                                            value = "{\"name\": \"John Doe\", \"email\": \"joindoe@gmail.com\", \"password\": \"123457\", \"role\": \"USER\"}"
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<?> authModify(@PathVariable(value = "action") String action,
                                        @PathVariable(value = "id", required = false) String id,
                                        @RequestBody Object req
    ) {
        return new ResponseEntity<>(authService.authGeneral(action, id, req), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/{action}")
    @Operation(
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "action",
                            schema = @Schema(allowableValues = {"address", "bill"})
                    )
            })
    public ResponseEntity<?> authDelete(@PathVariable(value = "action") String action,
                                        @RequestBody String[] ids) {
        return new ResponseEntity<>(authService.authDelete(action, ids), HttpStatus.OK);
    }
}
