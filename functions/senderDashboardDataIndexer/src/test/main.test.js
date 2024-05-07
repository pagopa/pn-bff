import { handleEvent } from '../app/eventHandler.js';
import { expect } from 'chai';
// const fs = require('fs');
// const axios = require('axios');
// var MockAdapter = require('axios-mock-adapter');
// var mock = new MockAdapter(axios);

describe('eventHandler tests', function () {
  it('statusCode 200', async () => {

    // process.env = Object.assign(process.env, {
    //   PN_DELIVERY_URL: 'https://api.dev.notifichedigitali.it',
    // });

    // const iunValue = '12345';

    // let url = `${process.env.PN_DELIVERY_URL}/notifications/sent/${iunValue}`;

    // mock
    //   .onGet(url)
    //   .reply(200, notification, { 'Content-Type': 'application/json' });

    const event = {
    //   pathParameters: { iun: iunValue },
    //   headers: {},
    //   requestContext: {
    //     authorizer: {},
    //   },
    //   resource: '/notifications/sent/{iun}',
    //   path: '/delivery/notifications/sent/MOCK_IUN',
    //   httpMethod: 'GET',
    };
    const context = {};

    // const response = await handleEvent(event, context);
    // expect(response.statusCode).to.be.equal(200);
  });
});
