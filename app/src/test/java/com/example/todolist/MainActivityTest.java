package com.example.todolist;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class MainActivityTest {

    @Mock
    private FirebaseAuth mAuth;

    @Mock
    private Context mContext;

    @Mock
    private Activity mActivity;

    int number1 = 10;
    int number2 = 3;

    @InjectMocks
    private MainActivity mMainActivity;


    @Test
    public void testMultiplication() {
        assertEquals(30, 10 * 3);
    }



}