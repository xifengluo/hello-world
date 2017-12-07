package com.itheima.action;

import java.io.IOException;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.service.IOrderService;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.org.apache.regexp.internal.recompile;

@Controller // 注解 交给spring管理
@Scope("prototype") // 每次请求都会产生一个action 故为多例
@Namespace("/order") // 表空间
@ParentPackage("struts-default") // 默认struts
public class OrderAction extends ActionSupport {

	@Autowired
	private IOrderService orderService;

	/*@Action(value = "delOrder", results = {
			@Result(name = "success", location = "findAllCustomer", type = "redirectAction") })*/
	@Action("delOrder")
	public String delOrder() {
         //调取service 层方法
		String orderNum = ServletActionContext.getRequest().getParameter("orderNum");
		orderService.delOrder(orderNum);
		return SUCCESS;
	}

	@Action("findOrder")
	public void findOrder() {
		ServletActionContext.getResponse().setCharacterEncoding("utf-8");
		// 获取id
		Integer customerId = Integer.parseInt(ServletActionContext.getRequest().getParameter("customerId"));

		// 得到当前页码
		Integer pageNum = Integer.parseInt(ServletActionContext.getRequest().getParameter("pageNum"));
		// 得到每页显示条数
		Integer currentCount = Integer.parseInt(ServletActionContext.getRequest().getParameter("currentCount"));
		// 调取service层方法 根据id查询订单信息

		// List<Order> list = orderService.findOrderByCustomer(customerId);
		PageBean<Order> pageBean = orderService.findOrderByCustomerPage(customerId, pageNum, currentCount);
		// 转换成json数据 回写客户端
		// 过滤
		PropertyFilter filter = new PropertyFilter() {

			@Override
			public boolean apply(Object arg0, String fieldName, Object arg2) {
				if ("cusPhone".equalsIgnoreCase(fieldName)) {
					return false;
				}
				if ("id".equalsIgnoreCase(fieldName)) {
					return false;
				}
				if ("orders".equalsIgnoreCase(fieldName)) {
					return false;
				}

				return true;
			}
		};

		String json = JSONArray.toJSONString(pageBean, filter, SerializerFeature.DisableCircularReferenceDetect);
		System.out.println(json);
		try {
			ServletActionContext.getResponse().getWriter().write(json);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
