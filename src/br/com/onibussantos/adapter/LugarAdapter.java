package br.com.onibussantos.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.com.onibussantos.VO.LugarVO;

public class LugarAdapter extends BaseAdapter {

	private Context ctx;
	private List<LugarVO> lista;

	public LugarAdapter(Context ctx, List<LugarVO> lista){
		
		this.ctx = ctx;
		this.lista = lista;
	}
	
	@Override
	public int getCount() {
		return lista.size();
	}

	@Override
	public Object getItem(int position) {

		return lista.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		
		LugarVO vo = (LugarVO) getItem(position);
		LayoutInflater layout = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layout.inflate(android.R.layout.simple_list_item_checked, null);
		
		TextView txtNomeLista = (TextView)v.findViewById(android.R.id.text1);
				
		//txtNomeLista.setText(vo.getLatitude().toString());
		
		return v;
	}

}
