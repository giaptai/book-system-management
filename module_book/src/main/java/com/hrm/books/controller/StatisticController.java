package com.hrm.books.controller;

import com.hrm.books.service.impl.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticController {
    final StatisticService statisticService;

    @GetMapping("/today")
    public ResponseEntity<Object> getByDay() {
        return new ResponseEntity<>(statisticService.getByDay(), HttpStatus.OK);
    }

    @Operation(
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "month",
                            schema = @Schema(
                                    allowableValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}
                            )
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "year",
                            schema = @Schema(
                                    allowableValues = {"2020", "2021", "2022", "2023", "2024"}
                            )
                    )
            }
    )
    @GetMapping("/monthly")
    public ResponseEntity<Object> getByMonth(@RequestParam(name = "month") int month, @RequestParam(value = "year") int year) {
        return new ResponseEntity<>(statisticService.getByMonth(month, year), HttpStatus.OK);
    }

    @Operation(
            parameters = @Parameter(
                    in = ParameterIn.PATH,
                    name = "year",
                    schema = @Schema(
                            allowableValues = {"2020", "2021", "2022", "2023", "2024"}
                    )
            )
    )
    @GetMapping("/yearly/{year}")
    public ResponseEntity<Object> getByYear(@PathVariable(name = "year") int year) {
        return new ResponseEntity<>(statisticService.getByYear(year), HttpStatus.OK);
    }
}
