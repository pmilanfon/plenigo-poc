package com.plenigo.order_lister.command;

import com.plenigo.order_lister.service.PlenigoService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class PlenigoCommand {

    private final PlenigoService plenigoService;

    @ShellMethod("Fetch orders and write to CSV")
    public void fetchAndWriteOrders() {
        plenigoService.processOrders();
    }
}
