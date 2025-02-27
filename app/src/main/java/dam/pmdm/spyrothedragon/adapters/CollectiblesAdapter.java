package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private Context context;
    private static int numClick=0;

    public CollectiblesAdapter(List<Collectible> collectibleList, Context context) {
        this.list = collectibleList;
        this.context = context;

    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;
        long tiempoCLick = 0;

        Context context;


        public CollectiblesViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
            /*
            Crear el Easter Egg de la animación
            Vamos a crear un listener para controlar el click.
            verificamos que el click se ha realizado sobre el item de la gema
            si se sigue dando en el item "Gemas" vamos sumando click. Si se pulsa en cualquier otro sitio el contador vuelve a cero.
            Si alcanzamos 4 click seguidos en "Gemas" lanzamos el easter egg del video.
             */

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nameTextView.getText().equals("Gemas")) {
                        numClick++;
                        if (numClick == 4) {
                            Navigation.findNavController(view).navigate(R.id.action_navigation_collectibles_to_videoFragment);
                            numClick=0;
                        }
                    } else {
                        numClick = 0;

                    }
                    Log.i("MiApp","El numero de click es de "+numClick);
                }
            });

        }


    }
}
