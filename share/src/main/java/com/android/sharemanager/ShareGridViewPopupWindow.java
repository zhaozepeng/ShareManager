package com.android.sharemanager;

import android.app.Activity;
import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class ShareGridViewPopupWindow extends PopupWindow {

	private View mMenuView;
	private LayoutInflater inflater;
	private GridView gridView;
	private TextView tv_dismiss;
	private Activity context;
	private View view;

	public ShareGridViewPopupWindow(Activity context, final IShareClickCallback callback) {
		super();
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.share_popupwindow, null);

		gridView = (GridView) mMenuView.findViewById(R.id.gridview);
		tv_dismiss = (TextView) mMenuView.findViewById(R.id.tv_dismiss);

		gridView.setAdapter(new GridViewAdapter());
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (callback != null)
					callback.onShareCallback(position);
				dismiss();
			}
		});

		this.setAnimationStyle(R.style.share_PopupAnimation);
		//http://stackoverflow.com/questions/3121232/android-popup-window-dismissal
		this.setBackgroundDrawable(new BitmapDrawable(null,""));
		this.setFocusable(true);

		tv_dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);

		//点击popUpWindow其他部分消失
		mMenuView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

		//增加popUpWindow其他部分的灰色效果
		view = new View(context);
		view.setBackgroundColor(Color.parseColor("#b0000000"));
		((ViewGroup)context.getWindow().getDecorView().getRootView()).addView(view);
	}

	private class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return ShareManager.Share.values().length;
		}

		@Override
		public Object getItem(int position) {
			return ShareManager.Share.values()[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.share_grid_item, null);
			}

			convertView.findViewById(R.id.iv_image).setBackgroundResource(ShareManager.Share.values()[position].getDrawableId());
			((TextView)convertView.findViewById(R.id.tv_text)).setText(ShareManager.Share.values()[position].getName());
			return convertView;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		((ViewGroup)context.getWindow().getDecorView().getRootView()).removeView(view);
	}

	public interface IShareClickCallback {
		void onShareCallback(int position);
	}
}
