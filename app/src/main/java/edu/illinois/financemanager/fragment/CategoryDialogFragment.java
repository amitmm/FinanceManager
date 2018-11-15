package edu.illinois.financemanager.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CategoryDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> categories = new ArrayList<>();

    public CategoryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CategoryRepo cRepo = new CategoryRepo(getActivity());
        UserRepo uRepo = new UserRepo(getActivity());
        User user = uRepo.getUser();
        ArrayList<Category> categoryList = cRepo.getCategoryList(user.id);
        Log.d("SEARCH", String.valueOf(categoryList.size()));
        if (categoryList.size() == 0) {
            Category category = new Category();
            category.name = "No Category";
            category.userID = user.id;
            cRepo.insert(category);
            categoryList = cRepo.getCategoryList(user.id);
        }
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            categories.add(category.name);
        }
        final CharSequence[] cs = categories.toArray(new CharSequence[categories.size()]);
        Log.d("SEARCH", categories.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_category)
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onCategoryClick(CategoryDialogFragment.this, cs[which].toString());
                    }
                });
        builder.setCancelable(false);
        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onCategoryClick(DialogFragment dialog, String category);
    }

}
