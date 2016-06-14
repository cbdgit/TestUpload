package com.example.testsend;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<Bitmap> data = new ArrayList<Bitmap>();
	private GridView mGridView;
	private String photoPath;
	private Adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ����Ĭ��ͼƬΪ�Ӻ�
		Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
		data.add(bp);
		// �ҵ��ؼ�ID
		mGridView = (GridView) findViewById(R.id.gridView1);
		// ��Adapter
		adapter = new Adapter(getApplicationContext(), data, mGridView);
		mGridView.setAdapter(adapter);
		// ���õ�������¼�
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (data.size() == 10) {
					Toast.makeText(MainActivity.this, "ͼƬ��9������", Toast.LENGTH_SHORT).show();
				} else {
					if (position == data.size() - 1) {
						Toast.makeText(MainActivity.this, "���ͼƬ", Toast.LENGTH_SHORT).show();
						// ѡ��ͼƬ
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
						startActivityForResult(intent, 0x1);
					} else {
						Toast.makeText(MainActivity.this, "�����" + (position + 1) + " ��ͼƬ", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// ���ó����¼�
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				dialog(position);
				return true;
			}
		});

	}

	/*
	 * Dialog�Ի�����ʾ�û�ɾ������ positionΪɾ��ͼƬλ��
	 */
	protected void dialog(final int position) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("ȷ���Ƴ������ͼƬ��");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				data.remove(position);
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// ��ӦstartActivityForResult����ȡͼƬ·��
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x1 && resultCode == RESULT_OK) {
			if (data != null) {

				ContentResolver resolver = getContentResolver();
				try {
					Uri uri = data.getData();
					// ���￪ʼ�ĵڶ����֣���ȡͼƬ��·����
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, proj, null, null, null);
					// ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					// ����������ֵ��ȡͼƬ·��
					photoPath = cursor.getString(column_index);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(photoPath)) {
			Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(photoPath, 300, 300);
			data.remove(data.size() - 1);
			Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
			data.add(newBp);
			data.add(bp);
			//��·������Ϊ�գ���ֹ���ֻ����ߺ󷵻�Activity���ô˷���ʱ�����Ƭ
			photoPath = null;
			adapter.notifyDataSetChanged();
		}
	}

}
