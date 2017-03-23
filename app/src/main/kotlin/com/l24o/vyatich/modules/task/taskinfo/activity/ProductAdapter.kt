package com.l24o.vyatich.modules.task.taskinfo.activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.l24o.vyatich.R
import com.l24o.vyatich.data.realm.models.RealmProduct
import com.l24o.vyatich.data.rest.models.Product
import kotlinx.android.synthetic.main.item_product.view.*

/**
 * @author Alexander Popov on 03/02/2017.
 */
class ProductAdapter(val data: List<RealmProduct>) : RecyclerView.Adapter<ProductAdapter.VH>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindView(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return VH(view)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(product: RealmProduct) {
            itemView.apply {
                productName.text = product.prod + product.prod_s
                productCountAndUnit.text = "---"//"${product.count} ${product.product.unit}"
            }
        }
    }

}