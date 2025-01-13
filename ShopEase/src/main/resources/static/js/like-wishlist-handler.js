$(document).ready(function() {
    console.log('Document is ready');

    // Fetch CSRF token and header
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // Log CSRF token and header for debugging
    console.log('CSRF Token:', csrfToken);
    console.log('CSRF Header:', csrfHeader);

    // Check if CSRF token and header are correctly set
    if (!csrfToken || !csrfHeader) {
        console.error('CSRF token or header not found!');
        return;
    }

    // let productId = image.data("product-id");


    const productId = $("#product-id").val(); // Look for the globally available input

    // Handle click event on like icon
    $(".like-icon").click(function() {
        console.log('Like icon clicked');
        const image = $(this);



        // Step 3: If `productId` is still null or undefined, log an error and exit
        if (!productId) {
            console.error("Product ID could not be found. Please check your HTML structure.");
            return;
        }


        console.log('Current Image Source:', image.attr('src'));
        console.log('Product ID:', productId);

        let liked = true;
        let newSrc;

        if (image.attr('src').includes('full_heart.png')) {
            newSrc = '/images/empty_heart.png';
            liked = false;
        } else {
            newSrc = '/images/full_heart.png';
            liked = true;
        }

        image.attr('src', newSrc);
        console.log('New Image Source:', newSrc);

        // Send AJAX request to the server
        $.ajax({
            type: 'POST',
            url: '/product-reaction',
            data: JSON.stringify({ liked: liked, productId: productId }),
            contentType: 'application/json',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
                console.log('CSRF Header Set:', csrfHeader);
                console.log('CSRF Token Set:', csrfToken);
            },
            success: function(response) {
                console.log('AJAX Success:', response);
            },
            error: function(error) {
                console.error('AJAX Error:', error);
                console.log('Error Details:', error.responseText); // Log error details
            }
        });
    });
    $(".wishlist-icon").click(function() {
        console.log('wishlist icon clicked');
        const image = $(this);


        let addToWishList = true;
        let newSrc;

        if (image.attr('src').includes('loaded-cart.png')) {
            newSrc = '/images/empty-cart.png';
            addToWishList = false;
        } else {
            newSrc = '/images/loaded-cart.png';
            addToWishList = true;
        }

        image.attr('src', newSrc);
        console.log('New Image Source:', newSrc);

        //Send AJAX request to the server
        $.ajax({
            type: 'POST',
            url: '/add-to-wishlist',
            data: JSON.stringify({ productId: productId }),
            contentType: 'application/json',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
                console.log('CSRF Header Set:', csrfHeader);
                console.log('CSRF Token Set:', csrfToken);
            },
            success: function(response) {
                console.log('AJAX Success:', response);
            },
            error: function(error) {
                console.error('AJAX Error:', error);
                console.log('Error Details:', error.responseText); // Log error details
            }
        });
    });
});
