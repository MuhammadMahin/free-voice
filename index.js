require("dotenv").config();
const express = require("express");
const twilio = require("twilio");

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const twilioNumber = process.env.TWILIO_NUMBER;

const client = new twilio(accountSid, authToken);


app.post("/start-call", async (req, res) => {
    const { caller, receiver } = req.body;

    if (!caller || !receiver) {
        return res.status(400).json({ success: false, error: "Both 'caller' and 'receiver' numbers are required." });
    }

    try {
        // Make sure the receiver is getting the call, not the caller.
        const call = await client.calls.create({
            to: receiver,  // FIX: Call should go to the receiver
            from: twilioNumber,
            url: `https://freevoice-a7e0e1a9084b.herokuapp.com/conference`
        });

        res.json({ success: true, message: "Call started, waiting for receiver to join.", callSid: call.sid });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});



app.post("/conference", (req, res) => {
    const { caller, receiver } = req.query;

    if (!caller || !receiver) {
        return res.status(400).send("<Response><Say>Error: Missing participants</Say></Response>");
    }

    const twiml = new twilio.twiml.VoiceResponse();
    twiml.say("Please wait while we connect the call.");
    twiml.dial().conference("LiveCallRoom");

    res.type("text/xml");
    res.send(twiml.toString());

    // Now, automatically call User B (Receiver)
    setTimeout(async () => {
        try {
            await client.calls.create({
                to: receiver,
                from: twilioNumber,
                url: "https://freevoice-a7e0e1a9084b.herokuapp.com/join-conference"
            });
        } catch (error) {
            console.error("Failed to call receiver:", error.message);
        }
    }, 2000);
});


app.post("/join-conference", (req, res) => {
    const twiml = new twilio.twiml.VoiceResponse();
    twiml.say("You are being connected to the caller.");
    twiml.dial().conference("LiveCallRoom");

    res.type("text/xml");
    res.send(twiml.toString());
});


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
