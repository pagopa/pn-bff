function extractTraceId(event, context) {
  return event.requestContext?.requestId || context.awsRequestId;
}

/**
 * Create an error HTTP response for API Gateway
 */
function createSuccessResponse(data, statusCode = 200) {
  return {
    statusCode,
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
    },
    body: JSON.stringify(data),
  };
}

/**
 * Create an error HTTP response for API Gateway
 */
function createErrorResponse(statusCode, message, event, context) {
  const traceId = extractTraceId(event, context);

  return {
    statusCode,
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
    },
    body: JSON.stringify({
      status: statusCode,
      title: "ERROR",
      traceId: traceId,
      errors: [
        {
          code: `PN_LOCATION_ERROR_${statusCode}`,
          detail: message,
        },
      ],
    }),
  };
}

module.exports = {
  createSuccessResponse,
  createErrorResponse,
};
