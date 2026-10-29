package com.example.travelsidequests.exceptionhandler

/**
 * For request-shape rules that Bean Validation cannot express on a single field,
 * such as "exactly one of destinationId and destinationName".
 */
class BadRequestException(message: String) : RuntimeException(message)
