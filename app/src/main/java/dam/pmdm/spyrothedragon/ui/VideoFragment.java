package dam.pmdm.spyrothedragon.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.FragmentVideoBinding;


public class VideoFragment extends Fragment {

private FragmentVideoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentVideoBinding.inflate(inflater,container, false);
        binding.videoView.setVideoPath("android.resource://" + requireActivity().getPackageName() + "/"+ R.raw.videospyro);
        binding.videoView.start();

        binding.botonDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getView());
                navController.popBackStack();

            }
        });

        return binding.getRoot();
    }
}