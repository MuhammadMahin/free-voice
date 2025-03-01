package com.uniqueideas.freevoice;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText phoneNumberInput;
    private Button callButton, toggleSpeakerButton;
    private ApiService apiService;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setupAudio();  // Ensure proper audio routing on launch

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        callButton = findViewById(R.id.callButton);
        toggleSpeakerButton = findViewById(R.id.toggleSpeakerButton); // Add this in XML

        apiService = RetrofitClient.getClient().create(ApiService.class);

        callButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                makeCall(phoneNumber);
            } else {
                Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
            }
        });

        toggleSpeakerButton.setOnClickListener(v -> toggleSpeakerphone());
    }

    private void makeCall(String number) {
        String callerNumber = "+16787103252"; // Your Twilio phone number as the caller

        CallRequest request = new CallRequest(callerNumber, number);
        apiService.makeCall(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configureAudioForCall();
                    Toast.makeText(MainActivity.this, "Call initiated! Call SID: " + response.body().getCallSid(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Call failed! Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error! " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Network Error: " + t.getMessage());
            }
        });
    }




    private void setupAudio() {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);  // Default to speakerphone
    }

    public void configureAudioForCall() {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);  // Enable speaker during call
    }

    public void toggleSpeakerphone() {
        if (audioManager.isSpeakerphoneOn()) {
            audioManager.setSpeakerphoneOn(false); // Switch to earpiece
            Toast.makeText(this, "Switched to Earpiece", Toast.LENGTH_SHORT).show();
        } else {
            audioManager.setSpeakerphoneOn(true); // Switch to speaker
            Toast.makeText(this, "Switched to Speaker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetAudioSettings(); // Reset audio when activity is destroyed
    }

    private void resetAudioSettings() {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }
}
