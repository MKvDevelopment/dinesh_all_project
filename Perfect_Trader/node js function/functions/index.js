"use-strict";
const functions = require("firebase-functions");
const admin=require("firebase-admin");
admin.initializeApp(functions.config().firebase);
const db=admin.firestore();

exports.intraday_today_call = functions.firestore.document("Today_Call/{id}")
    .onCreate((change, context) => {
      const newt1Value = change.data().target1;
      const newt2Value = change.data().target2;
      // console.log("Error getting documents: ");
      const payload = {
        notification: {
          title: "Intraday New Call",
          body: "Target-1="+newt1Value+", Target-2="+newt2Value,
        }};
      return db.collection("User_List").get().then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
          //    console.log("Error getting documents: dddddd  ddddd  ");
          admin.messaging().sendToDevice(doc.data().device_id, payload);
        });
      })
          .catch((error) => {
            console.log("Error getting documents: ", error);
          });
    });

exports.positional_today_call = functions.firestore.
    document("Today_Positional_Call/{id}")
    .onCreate((change, context) => {
      const newt1Value = change.data().target1;
      const newt2Value = change.data().target2;
      const payload = {
        notification: {
          title: "Positional New Call",
          body: "Target-1="+newt1Value+", Target-2="+newt2Value,
        }};
      return db.collection("User_List").get().then((querySnapshot) => {
        querySnapshot.forEach((doc) => {
          admin.messaging().sendToDevice(doc.data().device_id, payload);
        });
      })
          .catch((error) => {
            console.log("Error getting documents: ", error);
          });
    });

exports.intraday_Update = functions.firestore
    .document("Today_Call/{id}")
    .onUpdate((change, context) => {
      const newt1Value = change.after.data().t1;
      const previoust1Value = change.before.data().t1;
      const newt2Value = change.after.data().t2;
      const previoust2Value = change.before.data().t2;
      const newSlValue = change.after.data().sl;
      const previousSlValue = change.before.data().sl;

      if (newt1Value!=previoust1Value) {
        if (newt1Value=="hit") {
          const payload = {
            notification: {
              title: "Intraday Call",
              body: "Enjoy Profit Target-1 Hit at "+change.after.data().target1,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
        }
      } else if (newt2Value!=previoust2Value) {
        if (change.after.data().t2=="hit") {
          const payload = {
            notification: {
              title: "Intraday Call",
              body: "Enjoy Profit Target-2 Hit at "+change.after.data().target2,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
        }
      } else if (newSlValue!=previousSlValue) {
        if (change.after.data().sl=="hit") {
          const payload = {
            notification: {
              title: "Intraday Call Unfortunately Stoploss Hit",
              body: "StopLoss is hit at "+
              change.after.data().stop_loss,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
        }
      }
    });

exports.positional_Update = functions.firestore
    .document("Today_Positional_Call/{id}")
    .onUpdate((change, context) => {
      const newt1Value = change.after.data().t1;
      const previoust1Value = change.before.data().t1;
      const newt2Value = change.after.data().t2;
      const previoust2Value = change.before.data().t2;
      const newSlValue = change.after.data().sl;
      const previousSlValue = change.before.data().sl;

      if (newt1Value!=previoust1Value) {
        if (change.after.data().t1=="hit") {
          const payload = {
            notification: {
              title: "Positional Call",
              body: "Enjoy Profit Target-1 Hit at "+change.after.data().target1,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
        }
      } else if (newt2Value!=previoust2Value) {
        if (change.after.data().t2=="hit") {
          const payload = {
            notification: {
              title: "Positional Call",
              body: "Enjoy Profit Target-2 Hit at "+change.after.data().target2,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
        }
      } else if (newSlValue!=previousSlValue) {
        if (change.after.data().sl=="hit") {
          const payload = {
            notification: {
              title: "Intraday Call Intraday Call Unfortunately Stoploss Hit",
              body: "StopLoss is hit at "+
              change.after.data().stop_loss,
            }};
          db.collection("User_List").get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              admin.messaging().sendToDevice(doc.data().device_id, payload);
            });
          })
              .catch((error) => {
                console.log("Error getting documents: ", error);
              });
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
