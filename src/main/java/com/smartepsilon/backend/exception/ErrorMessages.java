package com.smartepsilon.backend.exception;

class ErrorMessages {

    static final String NOT_FOUND_MESSAGE_TEMPLATE = "Repository with given owner [%s] and id [%s] has not been found!";

    static final String SERVICE_UNHEALTHY_MESSAGE_TEMPLATE = new StringBuilder()
            .append("Unhealthy service state detected after [%s] number of attemps! ")
            .append("Circuit breaker should redirect somewhere but currently no requirements are known regarding the matter.\n")
            .append("A default fallback is the original resource which was tried again the same number of times.\n")
            .append("Underlying exception was [%s].\nTry again later or contact external host administration staff!")
            .toString();
}
