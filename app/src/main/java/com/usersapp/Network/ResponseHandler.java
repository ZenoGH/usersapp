package com.usersapp.Network;

import java.io.IOException;

public interface ResponseHandler {
    void handleResponse(String response) throws IOException;
}
