package ru.ruscalworld.bortexel.economy;

import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.economy.Report;

public class ReportPusher extends Thread {

    private final Bortexel4J client;
    private final Report report;

    public ReportPusher(Bortexel4J client, Report report) {
        this.client = client;
        this.report = report;
        this.setName("Report Pusher Thread");
    }

    @Override
    public void run() {
        report.push(client);
    }
}
