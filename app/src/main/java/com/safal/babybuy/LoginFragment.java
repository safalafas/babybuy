package com.safal.babybuy;

import static androidx.navigation.Navigation.findNavController;

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

import com.safal.babybuy.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;
    UserViewModel model;

    public LoginFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(UserViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogin = binding.btnLogin;
        TextView txtRegisterLink = binding.txtRegisterLink;

        btnLogin.setOnClickListener(v -> {
                    String email = binding.edtEmail.getText().toString();
                    String password = binding.edtPassword.getText().toString();
                    User user;
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        String error = getString(R.string.error_empty_fields);
                        if (TextUtils.isEmpty(email) && (TextUtils.isEmpty(password))) {
                            binding.inlEmail.setError(error);
                            binding.inlPassword.setError(error);
                        } else {
                            if (TextUtils.isEmpty(email)) {
                                binding.inlEmail.setError(error);
                                binding.inlPassword.setError(null);
                            }
                            if (TextUtils.isEmpty(password)) {
                                binding.inlPassword.setError(error);
                                binding.inlEmail.setError(null);
                            }
                        }
                    } else {
                        binding.inlEmail.setError(null);
                        binding.inlPassword.setError(null);
                        user = model.getUser(email, password);
                        if (user == null) {
                            String error = getString(R.string.error_login);
                            binding.inlEmail.setError(error);
                            binding.inlPassword.setError(error);
                            return;
                        }
                        binding.inlEmail.setError(null);
                        binding.inlPassword.setError(null);
                        findNavController(v).navigate(R.id.action_loginFragment_to_dashboardFragment);
                    }
                }
        );
        txtRegisterLink.setOnClickListener(v -> findNavController(v).navigate(R.id.action_loginFragment_to_registrationFragment));
    }
}