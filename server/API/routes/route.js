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
      controller.get_temp(request, response, db);
   });

   // Add Temperature to db
   router.post('/dht', (request, response) => {
     controller.add_temp(request, response, db);
   });
};