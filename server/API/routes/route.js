'use strict';

const controller = require('../controllers/controller');

module.exports = function (router, db) {
   // using router.get() to prefix our path
   // url: http://localhost:3000/api/
   router.get('/', (request, response) => {
      controller.info(request, response);
   });

   router.get('/dht', (request, response) => {
      controller.get_temp(db, request, response);
   });
};