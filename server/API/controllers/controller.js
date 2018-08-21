'use strict';
const url = require('url');
const Moment = require('moment');

/**
 * Info about API
 * 
 * @param {*} req 
 * @param {*} res 
 */
exports.info = (req, res) => {
   res.send('Hello, welcome to root of APIv1');
}

//========================= BEACH ====================================

/**
 * Get user data DHT from collection
 * 
 * @param {Datastore} db 
 * @param {*} req 
 * @param {*} res 
 */
exports.get_temp_hum = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = parameters.user;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      if (startDate == undefined || endDate == undefined) {
         // INFO: retrieve all data
         console.log(user);
         db.beach.find({
            user: user,
            temperature: {
               $exists: true
            },
            humidity: {
               $exists: true
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      } else {
         // INFO: retrieve data between date
         db.beach.find({
            user: user,
            temperature: {
               $exists: true
            },
            humidity: {
               $exists: true
            },
            $where: function () {
               return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      }
   }
};

/**
 * Add temperature and humidity to datastore
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_temp_hum = (req, res, db) => {
   let data = req.body;

   if (data.u == undefined || data.h == undefined || data.t == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let dht = {
         user: data.u,
         humidity: data.h,
         temperature: data.t,
         date: new Date()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }

}

/**
 * Get User UVA data from collection
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_uva = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const user = urlParts.user;
   const startDate = parameters.start;
   const endDate = parameters.end;

   if (user == undefined) {
      res.send("Username not specified!!");
   } else {
      if (startDate == undefined || endDate == undefined) {
         // INFO: retrieve all data
         db.beach.find({
            user: user,
            uva: {
               $exists: true
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      } else {
         // INFO: retrieve data between date
         db.beach.find({
            user: user,
            uva: {
               $exists: true
            },
            $where: function () {
               return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
            }
         }, function (err, data) {
            res.json({
               data: data
            });
         });
      }
   }
}

/**
 * Add User UVA data to collection
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_uva = (req, res, db) => {
   let data = req.body;

   if (data.u == undefined || data.l == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let dht = {
         user: data.u,
         uva: data.l,
         date: new Date()
      };
      db.beach.insert(dht, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }

}

//==================================== SEA ====================

/**
 * Get Water Temperature for sea
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_temp = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;
   if (startDate == undefined || endDate == undefined) {
      // INFO: retrieve all data
      db.sea.find({
         watertemp: {
            $exists: true
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      // INFO: retrieve data between date
      db.sea.find({
         watertemp: {
            $exists: true
         },
         $where: function () {
            return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   }
}


/**
 * Add water temperature to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_temp = (req, res, db) => {
   let data = req.body;

   if (data.t == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let obj = {
         temperature: data.t,
         date: new Date()
      };
      db.sea.insert(obj, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }
}

/**
 * Get water turbidity from collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_turb = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;
   if (startDate == undefined || endDate == undefined) {
      // INFO: retrieve all data
      db.sea.find({
         turbidity: {
            $exists: true
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      // INFO: retrieve data between date
      db.sea.find({
         turbidity: {
            $exists: true
         },
         $where: function () {
            return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   }
}

/**
 * Add water turbidity to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_turb = (req, res, db) => {
let data = req.body;

if (data.t == undefined) {
   res.json({
      message: data,
      error: "Incomplete data!"
   });
} else {
   let obj = {
      turbidity: data.t,
      date: new Date()
   };
   db.sea.insert(obj, function (err, newDoc) {
      res.json({
         message: newDoc,
         error: err
      });
   });
}
}

/**
 * Get waves movement from gyroscope and accelerometer from collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_waves_acc = (req, res, db) => {
   const urlParts = url.parse(req.url, true);
   const parameters = urlParts.query;
   const startDate = parameters.start;
   const endDate = parameters.end;
   if (startDate == undefined || endDate == undefined) {
      // INFO: retrieve all data
      db.sea.find({
         waves: {
            $exists: true
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   } else {
      // INFO: retrieve data between date
      db.sea.find({
         waves: {
            $exists: true
         },
         $where: function () {
            return Moment(this.date).isBetween(Moment(startDate), Moment(endDate));
         }
      }, function (err, data) {
         res.json({
            data: data
         });
      });
   }
}

/**
 * TODO: Add waves movement and data to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_waves_acc = (req, res, db)=>{
   let data = req.body;

   if (data.a == undefined || data.g == undefined) {
      res.json({
         message: data,
         error: "Incomplete data!"
      });
   } else {
      let obj = {
         //TODO impostare waves
         waves: 'calmo|mosso|molto mosso',
         acc: data.a,
         gyro: data.g,
         date: new Date()
      };
      db.sea.insert(obj, function (err, newDoc) {
         res.json({
            message: newDoc,
            error: err
         });
      });
   }
}