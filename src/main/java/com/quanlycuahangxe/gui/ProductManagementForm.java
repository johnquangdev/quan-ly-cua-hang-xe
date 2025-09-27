package com.quanlycuahangxe.gui;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField; // Thêm import này

import com.quanlycuahangxe.model.Brand;
import com.quanlycuahangxe.model.Category;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.impl.BrandServiceImpl;
import com.quanlycuahangxe.service.impl.CategoryServiceImpl;
import com.quanlycuahangxe.service.impl.ProductServiceImpl;
import com.quanlycuahangxe.service.interfaces.BrandService;
import com.quanlycuahangxe.service.interfaces.CategoryService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.CurrencyHelper;
import com.quanlycuahangxe.utils.ServiceResult;

public class ProductManagementForm extends JDialog {

    private JTextField txtName, txtPrice;
    private JTextArea txtDescription;
    private JComboBox<Category> comboCategory;
    private JComboBox<Brand> comboBrand;
    private JButton btnSave, btnAddCategory, btnAddBrand; // Khôi phục khai báo

    private ProductService productService = new ProductServiceImpl();
    private CategoryService categoryService = new CategoryServiceImpl();
    private BrandService brandService = new BrandServiceImpl();

    private Product product; // nếu edit
    private ProductManagementPanel parentPanel;
    private Window ownerWindow; // Thêm biến để lưu owner window

    // Thay đổi constructor để chấp nhận Window làm owner
    public ProductManagementForm(Window owner, Product product,
            ProductManagementPanel parentPanel) {
        super(owner, product == null ? "Thêm sản phẩm" : "Sửa sản phẩm",
                Dialog.ModalityType.APPLICATION_MODAL); // Sử dụng Dialog.ModalityType
        this.product = product;
        this.parentPanel = parentPanel;
        this.ownerWindow = owner; // Lưu owner window

        setSize(500, 450);
        setLocationRelativeTo(owner); // Đặt vị trí tương đối với owner
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField();
        txtPrice = new JTextField();
        txtDescription = new JTextArea(3, 20);
        comboCategory = new JComboBox<>();
        comboBrand = new JComboBox<>();
        btnSave = new JButton("Lưu");
        btnAddCategory = new JButton("+"); // Khôi phục khởi tạo
        btnAddBrand = new JButton("+"); // Khôi phục khởi tạo

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(txtName, gbc);
        row++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(txtPrice, gbc);
        row++;
        gbc.gridwidth = 1;

        // Đã xóa trường số lượng

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        add(comboCategory, gbc);
        gbc.gridx = 2;
        add(btnAddCategory, gbc); // Khôi phục thêm vào layout
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1;
        add(comboBrand, gbc);
        gbc.gridx = 2;
        add(btnAddBrand, gbc); // Khôi phục thêm vào layout
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(new JScrollPane(txtDescription), gbc);
        row++;
        gbc.gridwidth = 1;

        gbc.gridx = 1;
        gbc.gridy = row;
        add(btnSave, gbc);

        // Load brand/category
        loadCategoriesAndBrands();

        // Nếu edit thì điền dữ liệu
        if (product != null) {
            txtName.setText(product.getName());
            txtPrice.setText(CurrencyHelper.formatVND(product.getPrice()));
            // txtStock đã bị xóa
            txtDescription.setText(product.getDescription());
            selectComboItem(comboCategory, product.getCategoryId());
            selectComboItem(comboBrand, product.getBrandId());
        }

        // Nút thêm Category
        btnAddCategory.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Tên Category mới:");
            if (name != null && !name.isBlank()) {
                categoryService.createCategory(name, "");
                loadCategoriesAndBrands(); // cập nhật combo
            }
        });

        // Nút thêm Brand
        btnAddBrand.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Tên Brand mới:");
            if (name != null && !name.isBlank()) {
                brandService.createBrand(name, "");
                loadCategoriesAndBrands(); // cập nhật combo
            }
        });

        btnSave.addActionListener(e -> saveProduct());
    }

    private void selectComboItem(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Category && ((Category) item).getId() == id) {
                combo.setSelectedIndex(i);
                break;
            }
            if (item instanceof Brand && ((Brand) item).getId() == id) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void loadCategoriesAndBrands() {
        comboCategory.removeAllItems();
        ServiceResult<List<Category>> catRes = categoryService.getAllCategories();
        if (catRes.isSuccess()) {
            for (Category c : catRes.getData()) {
                comboCategory.addItem(c);
            }
        }

        comboBrand.removeAllItems();
        ServiceResult<List<Brand>> brandRes = brandService.getAllBrands();
        if (brandRes.isSuccess()) {
            for (Brand b : brandRes.getData()) {
                comboBrand.addItem(b);
            }
        }
    }

    private void saveProduct() {
        try {
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            // int stock = Integer.parseInt(txtStock.getText().trim());
            String desc = txtDescription.getText().trim();

            if (comboCategory.getSelectedItem() == null || comboBrand.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Chọn Category và Brand hợp lệ!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int catId = ((Category) comboCategory.getSelectedItem()).getId();
            int brandId = ((Brand) comboBrand.getSelectedItem()).getId();

            ServiceResult<Product> res;
            if (product == null) {
                // Khi tạo mới, số lượng mặc định là 0
                res = productService.createProduct(name, brandId, catId, price, 0, desc);
            } else {
                // Khi cập nhật, không thay đổi số lượng. Số lượng chỉ thay đổi qua Nhập/Xuất kho.
                res = productService.updateProduct(product.getId(), name, brandId, catId, price,
                        desc);
            }

            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                parentPanel.loadProductData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, res.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nhập giá hợp lệ!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
