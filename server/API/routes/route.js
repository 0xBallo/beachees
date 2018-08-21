'use strict';

const controller = require('../controllers/controller');

module.exports = function (router, db) {
  // using router.get() to prefix our path
  // url: http://localhost:3001/api/
  router.get('/', (request, response) => {
    controller.info(request, response);
  });

  // Get User Temperatures
  router.get('/dht', (request, response) => {
    controller.get_temp_hum(request, response, db);
  });

  // Add User Temperature to db
  router.post('/dht', (request, response) => {
    controller.add_temp_hum(request, response, db);
  });

  // Get User UVA levels
  router.get('/uva', (request, response) => {
    controller.get_uva(request, response, sb);
  });

  // Add User UVA levels
  router.post('/uva', (request, response) => {
    controller.add_uva(request, response, sb);
  });
};