package com.saeedmpt.chatapp.ui.main

import com.saeedmpt.chatapp.utility.PageStatusView.pageStatusView
import com.saeedmpt.chatapp.ui.base.BaseFragment
import com.saeedmpt.chatapp.model.HomeModel
import androidx.fragment.app.FragmentActivity
import com.saeedmpt.chatapp.adapter.HomeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.saeedmpt.chatapp.R
import com.saeedmpt.chatapp.interfaces.ICallBack
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saeedmpt.chatapp.databinding.FragmentHomeBinding
import com.saeedmpt.chatapp.model.EventModel
import com.saeedmpt.chatapp.utility.MyConstants
import com.saeedmpt.chatapp.utility.PageStatusView
import com.saeedmpt.chatapp.ui.main.HomeFragment
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class HomeFragment : BaseFragment() {
    private lateinit var listModel: ArrayList<HomeModel>
    private lateinit var contInst: FragmentActivity
    private var adapter: HomeAdapter? = null
    private lateinit var binding: FragmentHomeBinding
    private val searchString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)
        contInst = requireActivity()
        initAdapter()
        return binding.getRoot()
    }

    private fun initAdapter() {
        adapter = HomeAdapter(contInst)
        adapter!!.setiCallBack(object : ICallBack<HomeModel> {
            override fun callBack(model: HomeModel) {
                openCamera(model)
            }
        })
        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager =
            GridLayoutManager(contInst, 1, RecyclerView.VERTICAL, false)
        binding.rvList.adapter = adapter
        getData(searchString)
    }

    private fun openCamera(model: HomeModel) {
        val eventModel = EventModel()
        eventModel.action = EventModel.VARIABLE.ACTION_FILTER
        eventModel.model = model
        EventBus.getDefault().post(eventModel)

    }

    private fun getData(search: String) {
        listModel = MyConstants.getFilters()
        adapter!!.data = listModel
        adapter!!.notifyItemInserted(0)
        /*RepositoryData.initConnection(
                Global.getApiService().getApi().getGroupList(search
                ), new CounteryPresenterListener<GroupListApi>() {
                    @Override
                    public void onSuccess(GroupListApi data) {
                        listModel = data.getGroups();
                     //   adapter.setData(listModel);
                    //    adapter.notifyDataSetChanged();
                        //after get data connect socket
                    }

                    @Override
                    public void onFailure(Call<GroupListApi> call, Throwable t) {
                        MyUtils.setToastMessageResourse(contInst, R.string.errorserver);
                        pageStatus(binding.layoutWatinig.rlRetry);
                    }

                    @Override
                    public void onRetry(GroupListApi data) {
                        pageStatus(binding.layoutWatinig.rlRetry);
                    }

                    @Override
                    public void onLoading() {
                        pageStatus(binding.layoutWatinig.rlLoading);
                    }

                    @Override
                    public void doneLoading() {
                        pageStatus();
                    }

                    @Override
                    public void onNoWifi() {
                        pageStatus(binding.layoutWatinig.rlNoWifi);
                    }

                    @Override
                    public void onErrorBody(@Nullable ResponseBody errorBody) {
                        MyUtils.handelErrorBody(contInst, errorBody);
                        pageStatus(binding.layoutWatinig.rlRetry);
                    }

                });*/
    }

    private fun pageStatus(vararg visibleView: View?) {
        //if show main view pass null for params
        val viewsGone = arrayOf<View>(
            binding.layoutWatinig.rlLoading,
            binding.layoutWatinig.rlNoWifi,
            binding.layoutWatinig.rlNoData,
            binding.layoutWatinig.rlNoData2,
            binding.layoutWatinig.rlNoData3,
            binding.layoutWatinig.rlNoData4,
            binding.layoutWatinig.rlPvLoading,
            binding.layoutWatinig.rlRetry)
        pageStatusView(visibleView, viewsGone)
        binding.layoutWatinig.tvRetry.setOnClickListener { v: View? -> getData(searchString) }
        binding.layoutWatinig.tvAllTryconnection.setOnClickListener { v: View? ->
            getData(searchString)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        fun newInstance(strClsCat: String?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, strClsCat)
            fragment.arguments = args
            return fragment
        }
    }
}