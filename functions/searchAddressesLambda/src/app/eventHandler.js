const { getAutocompleteAddresses } = require("./autocomplete");
const { getPlaceCoordinates } = require("./getPlace");

async function handleEvent(event, context) {
  console.info("Processing API Gateway event:", JSON.stringify(event, null, 2));
  
  try {
    //exctract query params
    const resource = event.resource;
    const queryParams = event.queryStringParameters || {};
    
    let result;
    
    //route based on rsources
    if (resource === '/searchAddress') {
      const { address } = queryParams;
      
      if (!address) {
        return createErrorResponse(400, "Missing required parameter: address", event, context);
      }
      
      if (address.length < 2) {
        return createErrorResponse(400, "Address must be at least 2 characters long", event, context);
      }
      
      result = await getAutocompleteAddresses(address);
      
    } else if (resource === '/getPlaceCoordinates') {
      const { placeId } = queryParams;
      
      if (!placeId) {
        return createErrorResponse(400, "Missing required parameter: placeId", event, context);
      }
      
      result = await getPlaceCoordinates(placeId);
      
    } else {
      return createErrorResponse(404, `Route not found: ${resource}`, event, context);
    }
    
    // Return successful response
    return createSuccessResponse(result, event, context);
    
  } catch (error) {
    console.error("Error in handleEvent:", error);
    
    // aws location error handle
    if (error.name === 'ValidationException') {
      return createErrorResponse(400, `Invalid input: ${error.message}`, event, context);
    }
    
    if (error.name === 'AccessDeniedException') {
      return createErrorResponse(403, 'Access denied to AWS Location service', event, context);
    }
    
    if (error.name === 'ThrottlingException') {
      return createErrorResponse(429, 'Too many requests to AWS Location service', event, context);
    }
    
    // Generic server error
    return createErrorResponse(500, 'AWS Location service is unavailable', event, context);
  }
}


function extractTraceId(event, context) {
  return event.requestContext?.requestId || context.awsRequestId;
}


function createSuccessResponse(data, event, context, statusCode = 200) {
  return {
    statusCode,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type'
    },
    body: JSON.stringify(data)
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
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*'
    },
    body: JSON.stringify({
      status: statusCode,
      title: "ERROR",
      traceId: traceId,
      errors: [{ 
        code: `PN_LOCATION_ERROR_${statusCode}`, 
        detail: message 
      }]
    })
  };
}

module.exports = { handleEvent };
