package com.imedia.service.product;

import com.google.gson.Gson;
import com.imedia.oracle.entity.DetailProduct;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.oracle.repository.DetailProductRepository;
import com.imedia.oracle.repository.OrderDetailRepository;
import com.imedia.service.order.model.CreateOrderFile;
import com.imedia.service.order.model.CreateOrderFileProduct;
import com.imedia.service.order.model.CreateOrderReceiver;
import com.imedia.service.order.model.OrderDetailProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    static final Logger logger = LogManager.getLogger(ProductService.class);
    static final Gson gson = new Gson();
    private final DetailProductRepository productRepository;
    private final OrderDetailRepository shipOrderDetailRepository;

    @Autowired
    public ProductService(DetailProductRepository productRepository, OrderDetailRepository shipOrderDetailRepository) {
        this.productRepository = productRepository;
        this.shipOrderDetailRepository = shipOrderDetailRepository;
    }

    public List<DetailProduct> createDetailProduct(CreateOrderReceiver createOrderReceiver, SvcOrderDetail orderDetail) {
        productRepository.deleteProductByDetailCode(orderDetail.getSvcOrderDetailCode());
        List<DetailProduct> detailProducts = new ArrayList<>();
        for (OrderDetailProduct product : createOrderReceiver.getItems()) {
            DetailProduct detailProduct = new DetailProduct();
            detailProduct.setOrderDetailCode(orderDetail.getSvcOrderDetailCode());
            if (product.getCategory() != null)
                detailProduct.setProductCateId(BigDecimal.valueOf(product.getCategory()));
            else detailProduct.setProductCateId(BigDecimal.ONE);
            detailProduct.setName(product.getProductName());
            if (product.getProductValue() != null)
                detailProduct.setValue(product.getProductValue());
            else detailProduct.setValue(BigDecimal.ZERO);
            if (product.getCod() != null)
                detailProduct.setCod(product.getCod());
            else detailProduct.setCod(BigDecimal.ZERO);
            detailProduct.setQuantity(product.getQuantity());
            detailProducts.add(detailProduct);
        }
        List<DetailProduct> result = productRepository.saveAll(detailProducts);
        logger.info("=======PRODUCTS SAVE TO DB======" + gson.toJson(result));
        return result;
    }

    //Detail product for file
    public List<DetailProduct> createDetailProduct(CreateOrderFile createOrderFile, SvcOrderDetail orderDetail) {
        productRepository.deleteProductByDetailCode(orderDetail.getSvcOrderDetailCode());
        List<DetailProduct> detailProducts = new ArrayList<>();
        for (CreateOrderFileProduct product : createOrderFile.getProducts()) {
            DetailProduct detailProduct = new DetailProduct();
            detailProduct.setOrderDetailCode(orderDetail.getSvcOrderDetailCode());
//            detailProduct.setProductCateId(BigDecimal.valueOf(product.getCategory()));
            detailProduct.setName(product.getProductName());
            if (product.getProductValue() != null)
                detailProduct.setValue(product.getProductValue());
            else detailProduct.setValue(BigDecimal.ZERO);
            if (product.getCod() != null)
                detailProduct.setCod(product.getCod());
            else detailProduct.setCod(BigDecimal.ZERO);
            detailProduct.setQuantity(BigDecimal.valueOf(product.getQuantity()));
            if (product.getProductCateId() != null)
                detailProduct.setProductCateId(BigDecimal.valueOf(product.getProductCateId()));
            detailProducts.add(detailProduct);
        }
        List<DetailProduct> result = productRepository.saveAll(detailProducts);
        logger.info("=======PRODUCTS SAVE TO DB======" + gson.toJson(result));
        return detailProducts;
    }

    public void updateProducts(List<DetailProduct> oldProducts, CreateOrderReceiver createOrderReceiver, SvcOrderDetail orderDetail, boolean allowChangeCod) {
        for (DetailProduct oldProduct : oldProducts) {
            for (OrderDetailProduct item : createOrderReceiver.getItems()) {
                if (oldProduct.getId() == item.getId()) {
                    if (!item.getProductName().isEmpty())
                        oldProduct.setName(item.getProductName());
                    if (allowChangeCod) {
                        if (item.getQuantity() != null && !item.getQuantity().equals(BigDecimal.ZERO))
                            oldProduct.setQuantity(item.getQuantity());
                        if (item.getCod() != null) {
                            oldProduct.setChangedCod(oldProduct.getCod());
                            oldProduct.setCod(item.getCod());
                        } else
                            oldProduct.setCod(BigDecimal.ZERO);
                    }
                    productRepository.save(oldProduct);
                }
            }
        }
        if (allowChangeCod) {
//            BigDecimal totalCod = productRepository.getTotalCod(orderDetail.getSvcOrderDetailCode());
//            shipOrderDetailRepository.updateCod(totalCod, orderDetail.getSvcOrderDetailCode());
        }
    }
}
