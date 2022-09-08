package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerAdapter;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            HttpRequest request = HttpRequest.from(bufferedReader);
            HttpResponse response = processRequest(request);

            outputStream.write(response.toResponseString().getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processRequest(HttpRequest request) {
        Optional<Controller> controller = RequestMapping.getController(request.getUri());

        if (controller.isEmpty()) {
            return HttpResponse.notFound();
        }

        return ControllerAdapter.doService(controller.get(), request);
    }
}
