package etu.ensicaen.client.models;

import etu.ensicaen.client.ClientApplication;
import javafx.concurrent.Task;

public class MainMenu {
    public void host() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.host();
            }
        };

        task.setOnSucceeded(ev ->
                System.out.println(task.getValue())
        );

        new Thread(task).start();
    }

    public void join(String id) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ClientApplication.client.join(id);
            }
        };

        task.setOnSucceeded(ev ->
                System.out.println(task.getValue())
        );

        task.setOnFailed(ev ->
                System.out.println(task.getValue())
        );

        new Thread(task).start();
    }
}