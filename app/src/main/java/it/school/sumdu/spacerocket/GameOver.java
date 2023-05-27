package it.school.sumdu.spacerocket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameOver extends AppCompatActivity {
    private static final String TAG = "GameOver view: ";
    String userEmail;
    ImageView ivNewHighest;
    TextView tvPoints;
    TextView tvHighestText;
    TextView tvHighestValue;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over);
        ivNewHighest = findViewById(R.id.iNewHighest);
        tvPoints = findViewById(R.id.tvPoints);
        tvHighestText = findViewById(R.id.tvHighestText);
        tvHighestValue = findViewById(R.id.tvHighestValue);

        HashMap<Integer, TextView> viewByIdx = new HashMap<>();
        viewByIdx.put(0, findViewById(R.id.scoreOne));
        viewByIdx.put(1, findViewById(R.id.scoreTwo));
        viewByIdx.put(2, findViewById(R.id.scoreThree));

        userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        CollectionReference scoreRef = db.collection("points");

        scoreRef
        .whereEqualTo("email", userEmail)
        .get()
        .addOnCompleteListener((scoreTask) -> {
            if (!scoreTask.isSuccessful()) {
                Log.e(TAG, "error", scoreTask.getException());
            } else {
                List<DocumentSnapshot> scoreListByEmail = scoreTask.getResult().getDocuments();

                int highest;
                boolean isBigger = true;
                int score = getIntent().getExtras().getInt("points");

                if (scoreListByEmail.size() > 0) {
                    DocumentSnapshot ourScore = scoreListByEmail.get(0);
                    int previous = (int) ((long) ourScore.getData().getOrDefault("score", 0));
                    isBigger = score > previous;
                    highest = isBigger ? score : previous;
                    scoreRef.document(ourScore.getId()).update("score", highest);
                } else {
                    HashMap<String, Object> newScore = new HashMap<>();
                    newScore.put("email", userEmail);
                    newScore.put("score", score);
                    scoreRef.add(newScore);
                    highest = score;
                }

                if (isBigger) {
                    ivNewHighest.setVisibility(View.VISIBLE);
                    tvHighestText.setText("New record: ");
                }

                tvPoints.setText(String.valueOf(score));
                tvHighestValue.setText(String.valueOf(highest));


                scoreRef
                .orderBy("score", Direction.DESCENDING).limit(3).get()
                .addOnCompleteListener((topTask) -> {
                    if (!topTask.isSuccessful()) {
                        Log.e(TAG, "error", topTask.getException());
                    } else {
                        List<DocumentSnapshot> winners = topTask.getResult().getDocuments();
                        for (int i = 0; i < winners.size() ; i++) {
                            DocumentSnapshot winner = winners.get(i);
                            String winnerEmail = (String) (winner.get("email") == null ? "" : winner.get("email"));
                            String scoreRecord = String.valueOf(winner.get("score"));
                            Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+");
                            Matcher matcher = pattern.matcher(winnerEmail);
                            if (matcher.find()) {
                                viewByIdx.get(i).setText(matcher.group(0) + ": " + scoreRecord);
                            } else {
                                viewByIdx.get(i).setText(scoreRecord);
                            }
                        }
                    }
                });
            }
        });
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}
