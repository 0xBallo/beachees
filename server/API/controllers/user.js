'use strict';
const url = require('url');
const Notifier = require('../utils/notify-helper');
const Moment = require('moment');

/**
 * Register User device for push notifications
 * 
 * @param {*} req body.user and body.token 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_user_device = (req, res, db) => {
    const data = req.body;

    console.log(data);


    if (data.user === undefined || data.token === undefined) {
        res.status(401).send('Incomplete data in request!');
    } else {
        db.users.update({
            user: data.user
        }, {
            $set: {
                user: data.user
            },
            $set: {
                "smart.sensors": {
                    temp: Moment().format(),
                    uva: Moment().format(),
                    watert: Moment().format(),
                    turb: Moment().format(),
                    waves: Moment().format(),
                    wavesm: Moment().format()
                }
            },
            $push: {
                token: data.token
            }
        }, {
            upsert: true
        }, function (err, numReplaced, upsert) {

            if (err) {
                res.status(501).json(err);
            } else {
                res.status(201).json({
                    num: numReplaced
                });
            }
        });

    }
};

/**
 * INFO: debug method
 * Send notification to user's devices with message specified by request
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.send_push = (req, res, db, admin) => {
    const data = req.body;

    if (data.user === undefined || data.message === undefined || data.title === undefined || data.icon === undefined || data.color === undefined) {
        res.status(401).send('Incomplete data in request!');
    } else {
        db.users.find({
            user: data.user
        }, (err, results) => {
            if (err)
                res.status(501).send(err);
            if (results.length == 0) {
                res.status(202).send('No device/user found!');
            } else {
                results[0].token.forEach(t => {
                    const message = {
                        android: {
                            ttl: 3600 * 1000, // 1 hour in milliseconds
                            priority: 'normal',
                            notification: {
                                title: data.title,
                                body: data.message,
                                icon: data.icon,
                                color: data.color
                            }
                        },
                        token: t
                    };
                    console.log(message);
                    
                    Notifier.send_push(admin, db, data.user, message);

                });
                res.status(201).json(results);
            }
        });

    }

};

/**
 * Get notifications for specified user
 * 
 * @param {*} res 
 * @param {*} req 
 * @param {*} db 
 */
exports.get_notifications = (req, res, db) => {
    const urlParts = url.parse(req.url, true);
    const data = urlParts.query;

    if (data.user === undefined) {
        res.status(401).send('Incomplete data in request!');
    } else {

        db.notifies.find({
                $or: [{
                    user: data.user
                }, {
                    user: {
                        $exists: false
                    }
                }]
            })
            .exec(function (err, data) {
                if (err) {
                    res.status(501).json(err);
                } else {
                    res.status(201).json({
                        data: data
                    });
                }
            });

    }
};

/**
 * Remove notification with specified id from datbase
 * 
 * @param {} res 
 * @param {*} req 
 * @param {*} db 
 */
exports.del_notification = (req, res, db) => {
    const id = req.params.id;

    if (id === undefined) {
        res.send('Identifiers not specified!');
    } else {
        db.notifies.remove({
            _id: id
        }, {}, function (err, numRemoved) {
            if (err)
                res.json(err);
            if (numRemoved === 0) {
                res.status(202).send('No document find with specified id!');
            } else {
                res.send('Correctly removed ' + numRemoved + ' document/s!');
            }
        });
    }
};