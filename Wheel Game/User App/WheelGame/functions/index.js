const functions = require("firebase-functions");
// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

const database = admin.firestore();

exports.scheduledFunction=functions.pubsub.schedule("every")
    .onRun((context)=>{
      database.doc("App_Utils/Spin_time")
          .update({"time": admin.firestore.TimeStamp.now()});
      return console.log("Successfully Update"+admin.firestore.Timestamp.now());
    });
