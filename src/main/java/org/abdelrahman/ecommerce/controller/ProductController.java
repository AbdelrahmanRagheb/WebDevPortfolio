package org.abdelrahman.ecommerce.controller;

import org.abdelrahman.ecommerce.entity.*;
import org.abdelrahman.ecommerce.model.*;
import org.abdelrahman.ecommerce.service.AppService;
import org.abdelrahman.ecommerce.service.ProductUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    private final AppService appService;
    private ProductUtilityService productUtilityService;
    @Value("${product.categories}")
    private List<String> categories;
    int n = 50;

    @Autowired
    ProductController(AppService appService, ProductUtilityService productUtilityService) {
        this.appService = appService;
        this.productUtilityService = productUtilityService;
    }

    @GetMapping("/showAllProducts")
    public String showAllProducts(Model model, Principal principal) {
        List<Product> nProduct = appService.getNProduct(n);
        List<ProductView> productSearchRes = nProduct.stream()
                .map(ProductView::new)
                .toList();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            productSearchRes = productUtilityService.mapProductsToProductsViewHelper(user, nProduct);
        }
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("products", productSearchRes);

        return "products-page";
    }

    @GetMapping("/top-rated-products")
    public String showTopRatedProducts(Model model, Principal principal) {
        List<Product> products = appService.getTopRatedProducts();


        List<ProductView> searchRes = products.stream()
                .map(ProductView::new)
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            searchRes = productUtilityService.mapProductsToProductsViewHelper(user, products);
        }
        List<Map.Entry<String, Integer>> brands = productUtilityService.filterOnBrands(searchRes);
        List<Map.Entry<String, Integer>> ratings = productUtilityService.filterOnRating(searchRes);

        model.addAttribute("isAuthenticated", isAuthenticated);

        // Add the results and query to the model to display on the page
        model.addAttribute("products", searchRes);
        model.addAttribute("brands", brands);
        model.addAttribute("ratings", ratings);

        return "products-page";
    }
    @GetMapping("/products-under-100")
    public String showProductsUnder100$(Model model, Principal principal) {
        List<Product> products = appService.getProductsUnderPrice(100);


        List<ProductView> searchRes = products.stream()
                .map(ProductView::new)
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            searchRes = productUtilityService.mapProductsToProductsViewHelper(user, products);
        }
        List<Map.Entry<String, Integer>> brands = productUtilityService.filterOnBrands(searchRes);
        List<Map.Entry<String, Integer>> ratings = productUtilityService.filterOnRating(searchRes);

        model.addAttribute("isAuthenticated", isAuthenticated);

        // Add the results and query to the model to display on the page
        model.addAttribute("products", searchRes);
        model.addAttribute("brands", brands);
        model.addAttribute("ratings", ratings);

        return "products-page";
    }


    @GetMapping("/add-product")
    public String addProduct(Model model) {
        model.addAttribute("categories", categories);
        return "add-product-page";
    }

    @PostMapping("/add-product")
    public String addProduct(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam double price,
                             @RequestParam String brand,
                             @RequestParam String category,
                             @RequestParam(required = false) MultipartFile image, Principal principal) {
        // Create the Product object with the provided details
        Product product = new Product(title, description, price, category, brand);

        // If an image was uploaded, save it (example of saving the file)
        if (image != null && !image.isEmpty()) {
            try {
                // Specify the path where the image will be saved
                Path path = Paths.get("uploads/" + image.getOriginalFilename());
                Files.write(path, image.getBytes());  // Save the image to the file system

                // Set the image path or filename to the product (you could store the file path or URL)
                product.setImagePath(path.toString());
            } catch (IOException e) {
                e.printStackTrace();  // Handle error if the file can't be saved
            }
        }

        // Add the product to the service layer (e.g., save to the database)
        User user = appService.getUserByUserName(principal.getName());
        user.addProduct(product);
        appService.updateUserInfo(user);

        return "redirect:/";  // Redirect to homepage after successful addition
    }


    @GetMapping("/product/{id}")
    public String showProductDetails(
            @PathVariable("id") Long id,
            Principal principal, Model model) {
        Product p = appService.getProduct(id);

        User user = null;
        boolean isAuth = productUtilityService.isAuthenticated();

        if (isAuth) {
            user = appService.getUserByUserName(principal.getName());
        }

        model.addAttribute("isAuth", isAuth);
        model.addAttribute("username", principal != null ? principal.getName() : null);
        List<Review> reviewsOnProduct = appService.getReviewsOnProduct(id);
        List<Product> products = new ArrayList<>();
        products.add(p);
        List<ProductView> productViews = productUtilityService.mapProductsToProductsViewHelper(user, products);

        model.addAttribute("product", productViews.getFirst());
        model.addAttribute("reviews", reviewsOnProduct);
        return "product-details-page";
    }

    @GetMapping("/get-reviews")
    @ResponseBody
    public List<ReviewJson> getReviews(@RequestParam Long productId) {
        List<Review> reviewsOnProduct = appService.getReviewsOnProduct(productId);
        List<ReviewJson> reviews = new ArrayList<>();


        reviewsOnProduct.forEach(rev -> reviews.add(new ReviewJson(rev.getComment(), rev.getRating(), rev.getProduct().getId(), rev.getUser().getUsername(), rev.getCreatedAt())));
        return reviews;
    }

    @PostMapping("/add-review")
    @ResponseBody
    public ReviewResponse addComment(@RequestBody ReviewJson review, Principal principal) {

        Product product = appService.getProduct(review.getProductId());
        User user = appService.getUserByUserName(principal.getName());

        Review rev = new Review(review.getComment(), review.getRating(), user, product);
        appService.addReview(review.getProductId(), rev);
        return new ReviewResponse("succeeded");
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model, Principal principal) {
        // Call the service method to search for products with the provided query
        if (keyword.isEmpty() || keyword.isBlank()) return "redirect:/";
        List<Product> products = appService.searchProducts(keyword);

        List<ProductView> searchRes = products.stream()
                .map(ProductView::new)
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            searchRes = productUtilityService.mapProductsToProductsViewHelper(user, products);
        }
        List<Map.Entry<String, Integer>> brands = productUtilityService.filterOnBrands(searchRes);
        List<Map.Entry<String, Integer>> ratings = productUtilityService.filterOnRating(searchRes);

        model.addAttribute("isAuthenticated", isAuthenticated);

        // Add the results and query to the model to display on the page
        model.addAttribute("products", searchRes);
        model.addAttribute("brands", brands);
        model.addAttribute("ratings", ratings);

        // This ensures the search query is available for the form
        model.addAttribute("keyword", keyword);


        return "products-page"; // The name of the Thymeleaf template to render
    }

    @GetMapping("/products/{category}")
    public String getProductsByCategory(@PathVariable String category, Model model, Principal principal) {
        // Fetch products by category
        List<Product> products = appService.getProductsByCategory(category);
        List<ProductView> allCategoryProduct = products.stream()
                .map(ProductView::new)
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            User user = appService.getUserByUserName(principal.getName());
            allCategoryProduct = productUtilityService.mapProductsToProductsViewHelper(user, products);
        }
        List<Map.Entry<String, Integer>> brands = productUtilityService.filterOnBrands(allCategoryProduct);
        List<Map.Entry<String, Integer>> ratings = productUtilityService.filterOnRating(allCategoryProduct);

        model.addAttribute("isAuthenticated", isAuthenticated);

        // Add the results and query to the model to display on the page
        model.addAttribute("products", allCategoryProduct);
        model.addAttribute("brands", brands);
        model.addAttribute("ratings", ratings);


        return "products-page"; // Name of the Thymeleaf template for the product listing page
    }


}