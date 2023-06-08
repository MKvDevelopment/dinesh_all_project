"use-strict";
const functions = require("firebase-functions");
const admin=require("firebase-admin");
admin.initializeApp(functions.config().firebase);
const db=admin.firestore();

exports.withdraw_Update = functions.firestore
    .document("Users_list/{id}")
    .onUpdate((change, context) => {
      const newtValue = change.after.data().withdraw;
      const previoustValue = change.before.data().withdraw;
      const device_id = change.after.data().device_id;

      if (newtValue!=previoustValue) {
        if (newtValue=="Success") {
          const payload = {
            notification: {
              title: "Withdraw Success",
              body: "Payment Deposit successfully in your account",
            }};
            admin.messaging().sendToDevice(device_id, payload);
        }
      }
    });

exports.chatting = functions.database.ref("/Notifications/{pushId}/{id}")
    .onCreate((snapshot, context) => {
      const tokenResult = snapshot.val().token;
      const payload = {
        notification: {
          title: "New mesaage received.",
          body: "New mesaage received from Executive, To see Open App",
        }};
      return admin.messaging().sendToDevice(tokenResult, payload);
    });
