package edu.engagement.application.Fragments;

import edu.engagement.application.MyGLRenderer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CircleDataFragment extends Fragment
{
    private GLSurfaceView view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = new GLSurfaceView(this.getActivity());
        view.setRenderer(new MyGLRenderer());
        return view;
    }
    
    public void onPause()
    {
        super.onPause();
        if(view != null)
        {
            view.onPause();
        }
    }
    
    public void onResume()
    {
        super.onResume();
        if(view != null)
        {
            view.onResume();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
}
