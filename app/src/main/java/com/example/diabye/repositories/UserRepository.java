package com.example.diabye.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diabye.models.User;
import com.example.diabye.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class UserRepository {

    private final MutableLiveData<Boolean> isRegisteringUserSuccessful = new MutableLiveData<>();
    private final MutableLiveData<String> errorRegisterMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoginUserSuccessful = new MutableLiveData<>();
    private final MutableLiveData<String> errorLoginMessage = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final FirebaseFirestore mFirestore;
    public UserRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getIsRegisteringUserSuccessful(){
        return isRegisteringUserSuccessful;
    }

    public LiveData<String> getRegisterErrorMessage(){
        return errorRegisterMessage;
    }

    public LiveData<Boolean> getIsLoginUserSuccessful(){
        return isLoginUserSuccessful;
    }

    public LiveData<String> getLoginErrorMessage(){
        return errorLoginMessage;
    }

    public LiveData<User> getUser(){
        return currentUser;
    }



    public void registerUser(String email, String password, String name){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult ->{
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User user = new User(
                                firebaseUser.getUid(),
                                name,
                                email
                        );
                        createUser(user);
                    }
                })
                .addOnFailureListener(e -> {
                    errorRegisterMessage.postValue(e.getMessage());
                    isRegisteringUserSuccessful.postValue(false);
                });

    }

    private void createUser(User user){
        mFirestore.collection(Constants.USERS)
                .document(user.getUserId())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(unused -> isRegisteringUserSuccessful.postValue(true))
                .addOnFailureListener(e -> {
                    errorRegisterMessage.postValue(e.getMessage());
                    isRegisteringUserSuccessful.postValue(false);
                });
    }



    private String getCurrentUserId(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = "";
        if(firebaseUser!=null){
            userId = firebaseUser.getUid();
        }
        return userId;
    }

    public void getCurrentUserDetails(){
        mFirestore.collection(Constants.USERS)
                .document(getCurrentUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot!=null){
                        User user = documentSnapshot.toObject(User.class);
                        currentUser.postValue(user);
                        isLoginUserSuccessful.postValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    errorLoginMessage.postValue(e.getMessage());
                    isLoginUserSuccessful.postValue(false);
                });
    }

    public void loginUser(String email, String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult ->{
                    getCurrentUserDetails();
                })
                .addOnFailureListener(e -> {
                    errorLoginMessage.postValue(e.getMessage());
                    isLoginUserSuccessful.postValue(false);
                });
    }


}
