'use strict';
const url = require('url');
const Moment = require('moment');
const CONF = require('../../config/config.json');
const Notifier = require('../utils/notify-helper');

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
	const user = parameters.user;
	const date = parameters.date;

	let queryDate;
	if (date == undefined) {
		// INFO: retrieve data of today
		queryDate = Moment().format('YYYY-MM-DD');
	} else {
		// INFO: retrieve data from a day
		queryDate = Moment(date).format('YYYY-MM-DD');
	}
	db.sea.find({
			user: user,
			watertemp: {
				$exists: true
			},
			date: queryDate,
			$where: function () {
				let hour = parseInt(this.hour);
				return hour >= 8 && hour <= 19;
			}
		})
		.sort({
			hour: 1
		})
		.exec(function (err, data) {
			let temp = 0,
				result = [];
			let sum_t = 0.0,
				count = 0;
			data.forEach(el => {
				if (temp !== parseInt(el.hour)) {
					if (count !== 0) {
						result.push({
							watertemp: sum_t / parseFloat(count),
							hour: temp
						});
					}
					temp = parseInt(el.hour);
					count = 0;
					sum_t = 0.0;
				}
				sum_t += parseFloat(el.watertemp);
				count++;
			});
			res.json({
				data: result
			});
		});
}


/**
 * Add water temperature to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_temp = (req, res, db, admin) => {
	let data = req.body;

	if (data.t == undefined) {
		res.json({
			message: data,
			error: "Incomplete data!"
		});
	} else {

		if (data.t > CONF.threshold.wth) {
			//INFO send water temperature high notify
			const message = {
				android: {
					ttl: 3600 * 1000, // 1 hour in milliseconds
					priority: 'normal',
					notification: {
						title: 'Temperatura del mare',
						body: 'Temperatura del mare elevata (' + data.t + '°C)!',
						icon: CONF.notify.alert.icon,
						color: CONF.notify.alert.color
					}
				}
			}
			Notifier.send_push_broadcast(admin, db, message);
			db.users.update({
				user: data.u
			}, {
				$set: {
					"smart.sensors.watert": Moment().format()
				}
			}, function (err, num, upsert) {
				if (err) {
					console.error(err);
				}
				//updated notifiers flags
			})
		} else if (data.t < CONF.threshold.wtl) {
			//INFO send water temperature low notify
			const message = {
				android: {
					ttl: 3600 * 1000, // 1 hour in milliseconds
					priority: 'normal',
					notification: {
						title: 'Temperatura del mare',
						body: 'Temperatura del mare molto bassa (' + data.t + '°C)!',
						icon: CONF.notify.alert.icon,
						color: CONF.notify.alert.color
					}
				}
			}
			Notifier.send_push_broadcast(admin, db, message);
			db.users.update({
				user: data.u
			}, {
				$set: {
					"smart.sensors.watert": Moment().format()
				}
			}, function (err, num, upsert) {
				if (err) {
					console.error(err);
				}
				//updated notifiers flags
			})
		} else {
			db.users.findOne({
				user: data.u
			}, function (err, doc) {
				if (err) {
					console.error(err);
				} else {
					if (Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (5)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che sei sottoposto ad una temperatura troppo elevata, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
					}
					if (Moment(doc.smart.sensors.uva).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (6)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che stai al sole, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
					}
					if (Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (3)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'le condizioni del mare sono ottime: pulito, calmo e di una giusta temperatura. Fatti un bel bagno!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.watert = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					} else {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (4)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'Il mare è pulito, la temperatura è quella ottimale. Goditi le onde intanto che non sono troppo alte!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.watert = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					}
				}
			});
		}


		const wt = {
			watertemp: data.t,
			date: Moment().format('YYYY-MM-DD'),
			hour: Moment().format('HH'),
			ISO: Moment().format()
		};
		db.sea.insert(wt, function (err, newDoc) {
			res.json({
				message: newDoc,
				error: err
			});
		});
	}
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_temp_now = (req, res, db) => {
	const urlParts = url.parse(req.url, true);
	const parameters = urlParts.query;
	const user = parameters.user;
	db.sea.find({
			user: user,
			watertemp: {
				$exists: true
			}
		})
		.sort({
			ISO: -1
		})
		.limit(1)
		.exec(function (err, data) {
			if (err) {
				res.send(err);
			} else {
				res.json({
					data: data
				});
			}
		});
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
	const date = parameters.date;
	const user = parameters.user;

	let queryDate;
	if (date == undefined) {
		// INFO: retrieve data of today
		queryDate = Moment().format('YYYY-MM-DD');
	} else {
		// INFO: retrieve data from a day
		queryDate = Moment(date).format('YYYY-MM-DD');
	}
	db.sea.find({
			user: user,
			turbidity: {
				$exists: true
			},
			date: queryDate,
			$where: function () {
				let hour = parseInt(this.hour);
				return hour >= 8 && hour <= 19;
			}
		})
		.sort({
			hour: 1
		})
		.exec(function (err, data) {
			let temp = 0,
				result = [];
			let sum_t = 0.0,
				count = 0;
			data.forEach(el => {
				if (temp !== parseInt(el.hour)) {
					if (count !== 0) {
						result.push({
							turbidity: sum_t / parseFloat(count),
							hour: temp
						});
					}
					temp = parseInt(el.hour);
					count = 0;
					sum_t = 0.0;
				}
				sum_t += parseFloat(el.turbidity);
				count++;
			});
			res.json({
				data: result
			});
		});
}


/**
 * Add water turbidity to collection
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_water_turb = (req, res, db, admin) => {
	let data = req.body;

	if (data.t == undefined) {
		res.json({
			message: data,
			error: "Incomplete data!"
		});
	} else {

		if (data.t > CONF.threshold.turb) {
			//INFO send water temperature notify
			const message = {
				android: {
					ttl: 3600 * 1000, // 1 hour in milliseconds
					priority: 'normal',
					notification: {
						title: 'Torbidità del mare',
						body: 'Torbidità del mare elevata (' + data.t + ')!',
						icon: CONF.notify.alert.icon,
						color: CONF.notify.alert.color
					}
				}
			}
			Notifier.send_push_broadcast(admin, db, message);
			db.users.update({
				user: data.u
			}, {
				$set: {
					"smart.sensors.turb": Moment().format()
				}
			}, function (err, num, upsert) {
				if (err) {
					console.error(err);
				}
				//updated notifiers flags
			})
		} else {
			db.users.findOne({
				user: data.u
			}, function (err, doc) {
				if (err) {
					console.error(err);
				} else {
					if (Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (5)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che sei sottoposto ad una temperatura troppo elevata, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
					}
					if (Moment(doc.smart.sensors.uva).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (6)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che stai al sole, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
					}
					if (Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (3)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'le condizioni del mare sono ottime: pulito, calmo e di una giusta temperatura. Fatti un bel bagno!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.turb = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					} else {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (4)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'Il mare è pulito, la temperatura è quella ottimale. Goditi le onde intanto che non sono troppo alte!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.turb = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					}
				}
			});
		}

		const turb = {
			turbidity: data.t,
			date: Moment().format('YYYY-MM-DD'),
			hour: Moment().format('HH'),
			ISO: Moment().format()
		};
		db.sea.insert(turb, function (err, newDoc) {
			res.json({
				message: newDoc,
				error: err
			});
		});
	}
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_water_turb_now = (req, res, db) => {
	const urlParts = url.parse(req.url, true);
	const parameters = urlParts.query;
	const user = parameters.user;
	db.sea.find({
			user: user,
			turbidity: {
				$exists: true
			}
		})
		.sort({
			ISO: -1
		})
		.limit(1)
		.exec(function (err, data) {
			if (err) {
				res.send(err);
			} else {
				res.json({
					data: data
				});
			}
		});
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
	const date = parameters.date;
	const user = parameters.user;

	let queryDate;
	if (date == undefined) {
		// INFO: retrieve data of today
		queryDate = Moment().format('YYYY-MM-DD');
	} else {
		// INFO: retrieve data from a day
		queryDate = Moment(date).format('YYYY-MM-DD');
	}
	db.sea.find({
			user: user,
			waves: {
				$exists: true
			},
			date: queryDate,
			$where: function () {
				let hour = parseInt(this.hour);
				return hour >= 8 && hour <= 19;
			}
		})
		.sort({
			hour: 1
		})
		.exec(function (err, data) {
			let temp = 0,
				result = [];
			let sum_w = 0.0,
				count = 0;
			data.forEach(el => {
				if (temp !== parseInt(el.hour)) {
					if (count !== 0) {
						result.push({
							waves: sum_w / parseFloat(count),
							hour: temp
						});
					}
					temp = parseInt(el.hour);
					count = 0;
					sum_w = 0.0;
				}
				sum_w += parseFloat(el.waves);
				count++;
			});
			res.json({
				data: result
			});
		});
}


function normalize(acc, gyro) {
	const hash = acc * Math.random() + gyro * Math.random();
	const OldRange = (200 - 0);
	const NewRange = (3 - 0);
	const NewValue = (((hash - 0) * NewRange) / OldRange) + 0;
	return Math.floor(NewValue);
}


/**
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.add_waves_acc = (req, res, db, admin) => {
	let data = req.body;

	if (data.a == undefined || data.g == undefined) {
		res.json({
			message: data,
			error: "Incomplete data!"
		});
	} else {
		const waves = normalize(data.a, data.g);
		if (waves > CONF.threshold.wave) {
			//INFO send water temperature notify
			const message = {
				android: {
					ttl: 3600 * 1000, // 1 hour in milliseconds
					priority: 'normal',
					notification: {
						title: 'Movimento del mare',
						body: 'Livello delle onde critico (' + waves + ')!',
						icon: CONF.notify.alert.icon,
						color: CONF.notify.alert.color
					}
				}
			}
			Notifier.send_push_broadcast(admin, db, message);
			db.users.update({
				user: data.u
			}, {
				$set: {
					"smart.sensors.waves": Moment().format(),
					"smart.sensors.wavesm": Moment().format()
				}
			}, function (err, num, upsert) {
				if (err) {
					console.error(err);
				}
				//updated notifiers flags
			})
		} else {
			db.users.findOne({
				user: data.u
			}, function (err, doc) {
				if (err) {
					console.error(err);
				} else {
					if (Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (5)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che sei sottoposto ad una temperatura troppo elevata, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
						// doc.smart.sensors.waves = Moment().format();
						// db.users.update({
						// 	_id: doc._id
						// }, doc, {}, function (err, numReplaced, upsert) {
						// 	if (err) {
						// 		console.error(err);
						// 	}
						// 	//updated notifiers flags
						// });
					}
					if (Moment(doc.smart.sensors.uva).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.temp).isBefore(Moment().subtract(3, 'hours')) && Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.waves).isBefore(Moment(doc.smart.sensors.wavesm))) {
						//INFO: SMART NOTIFY (6)
						const message = {
							android: {
								ttl: 3600 * 1000, // 1 hour in milliseconds
								priority: 'normal',
								notification: {
									title: 'Smart',
									body: 'Sono più di tre ore che stai al sole, e le condizioni del mare sono ottime. Fatti un bel bagno rinfrescante!',
									icon: CONF.notify.smart.icon,
									color: CONF.notify.smart.color
								}
							}
						}
						Notifier.send_push(admin, db, data.u, message);
						// doc.smart.sensors.uva = Moment().format();
						// db.users.update({
						// 	_id: doc._id
						// }, doc, {}, function (err, numReplaced, upsert) {
						// 	if (err) {
						// 		console.error(err);
						// 	}
						// 	//updated notifiers flags
						// });
					}
					if (waves <= 1) {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (3)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'le condizioni del mare sono ottime: pulito, calmo e di una giusta temperatura. Fatti un bel bagno!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.wavesm = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					} else {
						if (Moment(doc.smart.sensors.watert).isBefore(Moment().subtract(20, 'minutes')) && Moment(doc.smart.sensors.turb).isBefore(Moment().subtract(20, 'minutes'))) {
							//INFO: SMART NOTIFY (4)
							const message = {
								android: {
									ttl: 3600 * 1000, // 1 hour in milliseconds
									priority: 'normal',
									notification: {
										title: 'Smart',
										body: 'Il mare è pulito, la temperatura è quella ottimale. Goditi le onde intanto che non sono troppo alte!',
										icon: CONF.notify.smart.icon,
										color: CONF.notify.smart.color
									}
								}
							}
							Notifier.send_push(admin, db, data.u, message);
						}
						doc.smart.sensors.waves = Moment().format();
						db.users.update({
							_id: doc._id
						}, doc, {}, function (err, numReplaced, upsert) {
							if (err) {
								console.error(err);
							}
							//updated notifiers flags
						});
					}
				}
			});
		}

		const w = {
			waves: waves,
			date: Moment().format('YYYY-MM-DD'),
			hour: Moment().format('HH'),
			ISO: Moment().format()
		};
		db.sea.insert(w, function (err, newDoc) {
			res.json({
				message: newDoc,
				error: err
			});
		});
	}
}

/**
 * Get last data from Sea Temperature sensor
 * 
 * @param {*} req 
 * @param {*} res 
 * @param {*} db 
 */
exports.get_waves_now = (req, res, db) => {
	const urlParts = url.parse(req.url, true);
	const parameters = urlParts.query;
	const user = parameters.user;

	db.sea.find({
			user: user,
			waves: {
				$exists: true
			}
		})
		.sort({
			ISO: -1
		})
		.limit(1)
		.exec(function (err, data) {
			if (err) {
				res.status(501).send(err);
			} else {
				res.status(201).json({
					data: data
				});
			}
		});
}