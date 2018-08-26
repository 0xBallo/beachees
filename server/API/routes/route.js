'use strict';

const beachCtrl = require('../controllers/beach');
const usersCtrl = require('../controllers/user');
const seaCtrl = require('../controllers/sea');

module.exports = function (router, db) {
  // using router.get() to prefix our path
  // url: http://localhost:3001/api/
  router.get('/', (request, response) => {
    response.json(router.stack);
  });

  // Get User Temperatures
  router.get('/dht', (request, response) => {
    beachCtrl.get_temp_hum(request, response, db);
  });

  // Add User Temperature to db
  router.post('/dht', (request, response) => {
    beachCtrl.add_temp_hum(request, response, db);
  });

  // Get last data from dht
  router.get('/dht/now', (request, response) => {
    beachCtrl.get_dht_now(request, response, db);
  })

  // Get User UVA levels
  router.get('/uva', (request, response) => {
    beachCtrl.get_uva(request, response, db);
  });

  // Add User UVA levels
  router.post('/uva', (request, response) => {
    beachCtrl.add_uva(request, response, db);
  });

  // Get last data from UVA
  router.get('/uva/now', (request, response) => {
    beachCtrl.get_uva_now(request, response, db);
  });

  // Get sea Temps levels
  router.get('/sea/temp', (request, response) => {
    seaCtrl.get_water_temp(request, response, db);
  });

  // Add sea Temp levels
  router.post('/sea/temp', (request, response) => {
    seaCtrl.add_water_temp(request, response, db);
  });

  // Get last sea Temps
  router.get('/sea/temp/now', (request, response) => {
    seaCtrl.get_water_temp_now(request, response, db);
  });

  // Get sea Turbidity levels
  router.get('/sea/turbidity', (request, response) => {
    seaCtrl.get_water_turb(request, response, db);
  });

  // Add sea turbidity levels
  router.post('/sea/turbidity', (request, response) => {
    seaCtrl.add_water_turb(request, response, db);
  });

  // Get last sea Turbidity levels
  router.get('/sea/turbidity/now', (request, response) => {
    seaCtrl.get_water_turb_now(request, response, db);
  });

  // Get sea waves levels
  router.get('/sea/waves', (request, response) => {
    seaCtrl.get_waves_acc(request, response, db);
  });

  // Add sea waves levels
  router.post('/sea/waves', (request, response) => {
    seaCtrl.add_waves_acc(request, response, db);
  });

  // Get last sea waves levels
  router.get('/sea/waves/now', (request, response) => {
    seaCtrl.get_waves_now(request, response, db);
  });

  // User subscription for push notification
  router.post('/subscribe', (request, response) => {
    usersCtrl.add_user_device(request, response, db);
  });

  // Send push to User
  router.post('/push', (request, response) => {
    usersCtrl.send_push(request, response, db);
  });

};