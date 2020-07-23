package com.techtrickbd.nahidshop.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techtrickbd.nahidshop.R;
import com.techtrickbd.nahidshop.activities.PurchaseActivity;
import com.techtrickbd.nahidshop.adapters.Free_Fire_Adapter;
import com.techtrickbd.nahidshop.adapters.Pubg_Adapter;
import com.techtrickbd.nahidshop.models.Free_Fire_Model;
import com.techtrickbd.nahidshop.models.Pubg_Model;
import com.techtrickbd.nahidshop.utils.Users_Static;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class PubgFragment extends Fragment {

    public PubgFragment() {
        // Required empty public constructor
    }

    private Button buyNow;
    private RadioGroup radioGroup, paymentRadioGroup;
    private RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7, rb8, p1, p2, p3, p4;
    private EditText playerID;
    String splayerId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private CollectionReference free_fire;
    private RecyclerView recyclerView;
    private String TAG = "free_fire";
    String methodRB, diamondrb;
    private ProgressBar contentLoadingProgressBar;
    private List<Pubg_Model> pubg_models;
    private Pubg_Adapter pubg_adapter;
    private Context mContext;
    private DocumentReference priceRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pubg, container, false);
        casting(view);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
            }
        });
        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
            }
        });
        return view;
    }

    private void casting(View view) {
        buyNow = view.findViewById(R.id.pubg_buyNow);
        radioGroup = view.findViewById(R.id.pubg_select_radio_group);
        rb1 = view.findViewById(R.id.pubg_rb1);
        rb2 = view.findViewById(R.id.pubg_rb2);
        rb3 = view.findViewById(R.id.pubg_rb3);
        rb4 = view.findViewById(R.id.pubg_rb4);
        rb5 = view.findViewById(R.id.pubg_rb5);
        rb6 = view.findViewById(R.id.pubg_rb6);
        rb7 = view.findViewById(R.id.pubg_rb7);
        rb8 = view.findViewById(R.id.pubg_rb8);
        playerID = view.findViewById(R.id.pubg_player_id);
        paymentRadioGroup = view.findViewById(R.id.pubg_payment_rg);
        p1 = view.findViewById(R.id.pubg_bkash_rb);
        p2 = view.findViewById(R.id.pubg_rocket_rb);
        p3 = view.findViewById(R.id.pubg_nagad_rb);
        p4 = view.findViewById(R.id.pubg_nwallet_rb);
        recyclerView = view.findViewById(R.id.pubg_recyller);
        contentLoadingProgressBar = view.findViewById(R.id.pubg_progress);
        pubg_models = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        initPriceRange();
        initRecyller();
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splayerId = playerID.getText().toString();
                final int selectedID = radioGroup.getCheckedRadioButtonId();
                int paymentSelectedID = paymentRadioGroup.getCheckedRadioButtonId();
                if (splayerId.isEmpty()) {
                    Toasty.error(getContext(), "Type your player id", Toast.LENGTH_LONG).show();
                } else if (selectedID == -1) {
                    Toasty.error(getContext(), "Select an option", Toast.LENGTH_SHORT).show();
                } else if (paymentSelectedID == -1) {
                    Toasty.error(getContext(), "Please Select a payment method", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton diamondRB = radioGroup.findViewById(selectedID);
                    RadioButton paymentMethodRB = paymentRadioGroup.findViewById(paymentSelectedID);
                    methodRB = paymentMethodRB.getText().toString();
                    diamondrb = diamondRB.getText().toString();
                    if (methodRB.contains("N-Pay")) {
                        checkWallet();
                    } else {
                        googingtoPurchase();
                    }
                }

            }
        });

    }

    private void initPriceRange() {
        priceRef = db.collection("Price_Range").document("Pubg");
        priceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        rb1.setText(document.get("1").toString());
                        rb2.setText(document.get("2").toString());
                        rb3.setText(document.get("3").toString());
                        rb4.setText(document.get("4").toString());
                        rb5.setText(document.get("5").toString());
                        rb6.setText(document.get("6").toString());
                        rb7.setText(document.get("7").toString());
                        rb8.setText(document.get("8").toString());
                    } else {
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void initRecyller() {
        free_fire = db.collection("Pubg");
        free_fire
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Pubg_Model fireModel = document.toObject(Pubg_Model.class);
                                pubg_models.add(fireModel);
                                pubg_adapter = new Pubg_Adapter(pubg_models, mContext);
                                recyclerView.setAdapter(pubg_adapter);
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                recyclerView.setLayoutManager(horizontalLayoutManagaer);
                                contentLoadingProgressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void checkWallet() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Long tk = (long) document.get("tk");
                    if (tk <= 30) {
                        Toasty.error(getContext(), "you do not have sufficient Balance", Toast.LENGTH_SHORT).show();
                    } else {
                        googingtoPurchase();
                    }
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }

    private void googingtoPurchase() {
        Intent intent = new Intent(getActivity(), PurchaseActivity.class);
        intent.putExtra("gameid", splayerId);
        intent.putExtra("dimond", diamondrb);
        intent.putExtra("method", methodRB);
        intent.putExtra("from", "Pubg");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        pubg_models.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pubg_models.clear();
    }
}
