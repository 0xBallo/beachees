'use strict';
const Moment = require('moment');
const CONF = require('./config/config.json');

exports.populate_db = (db) => {
   for (let i = 0; i < 7; i++) {
      for (let j = 8; j < 20; j++) {

         //INFO: save DHT data
         const dht = {
            user: 'PM12',
            humidity: Math.random() * (CONF.debug.humidity.h - CONF.debug.humidity.l) + CONF.debug.humidity.l,
            temperature: Math.random() * (CONF.debug.temperature.h - CONF.debug.temperature.l) + CONF.debug.temperature.l,
            date: Moment().subtract(i, 'd').format('YYYY-MM-DD'),
            hour: j,
            ISO: Moment().subtract(i, 'd').hour(j).format()
         };
         db.beach.insert(dht);

         //INFO: save UVA data
         const uv = {
            user: 'PM12',
            uva: Math.random() * (CONF.debug.uv.h - CONF.debug.uv.l) + CONF.debug.uv.l,
            date: Moment().subtract(i, 'd').format('YYYY-MM-DD'),
            hour: j,
            ISO: Moment().subtract(i, 'd').hour(j).format()
         };
         db.beach.insert(uv);

         //INFO: save SEA TEMPERATURE data
         const wt = {
            watertemp: Math.random() * (CONF.debug.water.h - CONF.debug.water.l) + CONF.debug.water.l,
            date: Moment().subtract(i, 'd').format('YYYY-MM-DD'),
            hour: j,
            ISO: Moment().subtract(i, 'd').hour(j).format()
         };
         db.sea.insert(wt);

         //INFO: save SEA TURBIDITY data
         const turb = {
            turbidity: Math.random() * (CONF.debug.turbidity.h - CONF.debug.turbidity.l) + CONF.debug.turbidity.l,
            date: Moment().subtract(i, 'd').format('YYYY-MM-DD'),
            hour: j,
            ISO: Moment().subtract(i, 'd').hour(j).format()
         };
         db.sea.insert(turb);

         //INFO: save SEA WAVES data
         const w = {
            waves: Math.random() * (CONF.debug.waves.h - CONF.debug.waves.l) + CONF.debug.waves.l,
            date: Moment().subtract(i, 'd').format('YYYY-MM-DD'),
            hour: j,
            ISO: Moment().subtract(i, 'd').hour(j).format()
         };
         db.sea.insert(w);

      }
   }
};