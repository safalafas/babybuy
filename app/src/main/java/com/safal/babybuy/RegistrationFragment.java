package com.safal.babybuy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.safal.babybuy.databinding.FragmentRegistrationBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;
    private User user;
    private UserViewModel model;

    public RegistrationFragment() {
        //Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnRegister = binding.btnRegister;
        TextView txtLoginLink = binding.txtLoginLink;

        btnRegister.setOnClickListener(v -> {
                    String name = binding.edtName.getText().toString();
                    String email = binding.edtEmail.getText().toString();
                    String password = binding.edtPassword.getText().toString();
                    String confirmPwd = binding.edtCPassword.getText().toString();

                    if (nameFieldValidation(name) && emailFieldValidation(email) && passwordFieldValidation(password, confirmPwd)) {
                        user = new User(name, email, password);
                        model.registerUser(user);
                        Navigation.findNavController(v).navigate(R.id.action_registrationFragment_to_loginFragment);
                    }

                }
        );
        txtLoginLink.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registrationFragment_to_loginFragment));
    }

    private boolean nameFieldValidation(String email) {
        if (TextUtils.isEmpty(email)) {
            String error = getString(R.string.error_empty_fields);
            binding.inlName.setError(error);
            return false;
        }
        binding.inlName.setError(null);
        return true;
    }

    private boolean emailFieldValidation(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String error;
        if (TextUtils.isEmpty(email)) {
            error = getString(R.string.error_empty_fields);
            binding.inlEmail.setError(error);
            return false;
        } else if (!email.matches(regex)) {
            error = getString(R.string.email_error);
            binding.inlEmail.setError(error);
            return false;
        } else if (!model.checkEmail(email).isEmpty()) {
            error = getString(R.string.error_email_exists);
            binding.inlEmail.setError(error);
            return false;
        } else {
            binding.inlEmail.setError(null);
            return true;
        }
    }

    public boolean passwordFieldValidation(String password, String confirmPassword) {
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        String error;
        if (!matcher.matches()) {
            error = getString(R.string.password_error);
            binding.inlPassword.setError(error);
            return false;
        } else if (!password.equals(confirmPassword)) {
            binding.inlPassword.setError(null);
            error = getString(R.string.confirmation_error);
            binding.inlCPassword.setError(error);
            return false;
        } else {
            binding.inlPassword.setError(null);
            binding.inlCPassword.setError(null);
            return true;
        }
    }


}