'use strict';

const controller = require('../controllers/controller');

module.exports = function (router, db) {
  // using router.get() to prefix our path
  // url: http://localhost:3001/api/
  router.get('/', (request, response) => {
    response.json(router.stack);
  });

  // Get User Temperatures
  router.get('/dht', (request, response) => {
    controller.get_temp_hum(request, response, db);
  });

  // Add User Temperature to db
  router.post('/dht', (request, response) => {
    controller.add_temp_hum(request, response, db);
  });

  // Get last data from dht
  router.get('/dht/now', (request, response) => {
    controller.get_dht_now(request, response, db);
  })

  // Get User UVA levels
  router.get('/uva', (request, response) => {
    controller.get_uva(request, response, sb);
  });

  // Add User UVA levels
  router.post('/uva', (request, response) => {
    controller.add_uva(request, response, sb);
  });

  // Get last data from UVA
  router.get('/uva/now', (request, response) => {
    controller.get_uva_now(request, response, db);
  });

  // Get sea Temps levels
  router.get('/sea/temp', (request, response) => {
    controller.get_water_temp(request, response, sb);
  });

  // Add sea Temp levels
  router.post('/sea/temp', (request, response) => {
    controller.add_water_temp(request, response, sb);
  });

  // Get last sea Temps
  router.get('/sea/temp/now', (request, response) => {
    controller.get_water_temp_now(request, response, sb);
  });

  // Get sea Turbidity levels
  router.get('/sea/turbidity', (request, response) => {
    controller.get_water_turb(request, response, sb);
  });

  // Add sea turbidity levels
  router.post('/sea/turbidity', (request, response) => {
    controller.add_water_turb(request, response, sb);
  });

  // Get last sea Turbidity levels
  router.get('/sea/turbidity/now', (request, response) => {
    controller.get_water_turb_now(request, response, sb);
  });

  // Get sea waves levels
  router.get('/sea/waves', (request, response) => {
    controller.get_waves_acc(request, response, sb);
  });

  // Add sea waves levels
  router.post('/sea/waves', (request, response) => {
    controller.add_waves_acc(request, response, sb);
  });

  // Get last sea waves levels
  router.get('/sea/waves/now', (request, response) => {
    controller.get_waves_now(request, response, sb);
  });

};