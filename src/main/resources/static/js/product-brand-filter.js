$(document).ready(function () {
    console.log('Document is ready');

    // Fetch CSRF token and header
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");
    let brand='ALL';
    let rate=0.0;
    let price=0.0;
    // Log CSRF token and header for debugging
    console.log('CSRF Token:', csrfToken);
    console.log('CSRF Header:', csrfHeader);

    // Check if CSRF token and header are correctly set
    if (!csrfToken || !csrfHeader) {
        console.error('CSRF token or header not found!');
        return;
    }

    // Render stars dynamically based on rating
    $(".star-rating").each(function () {
        const rating = parseFloat($(this).closest('.product').find('.product-rating').val()) || 0.0;

        // Round the rating to one decimal place
        const roundedRating = rating.toFixed(1);

        // Calculate the number of full, partial, and empty stars
        const fullStars = Math.floor(rating);
        const fraction = rating - fullStars;
        const partialStar = (fraction >= 0.75) ? "¾" : (fraction >= 0.5) ? "½" : (fraction >= 0.25) ? "¼" : null;
        const emptyStars = 5 - fullStars - (partialStar ? 1 : 0);

        // Clear existing stars before adding new ones
        $(this).empty();

        // Add full stars
        for (let i = 0; i < fullStars; i++) {
            $(this).append('<span class="star full-star">★</span>');
        }

        // Add partial star if exists
        if (partialStar) {
            $(this).append(`<span class="star partial-star-${partialStar}">★</span>`);
        }

        // Add empty stars
        for (let i = 0; i < emptyStars; i++) {
            $(this).append('<span class="star empty-star">☆</span>');
        }

        // Add the rounded rating value beside the stars
        $(this).append(`<span class="rating-value"> (${roundedRating})</span>`);
    });


    // Brand filter function
    function filterByBrand(targetBrand) {
        $(".product").each(function () {
            const productBrand = $(this).find(".product-brand").val().toUpperCase();
            const productRating = parseFloat($(this).find(".product-rating").val());
            brand=targetBrand;
            if (targetBrand === 'All' || productBrand === targetBrand) {
                if (rate===0.0 || productRating===rate)$(this).show();
            } else{
                $(this).hide();
            }
        });
    }

    // Rating filter function
    function filterByRating(targetRating) {
        $(".product").each(function () {
            const productRating = parseFloat($(this).find(".product-rating").val());
            rate=targetRating;
            const productBrand = $(this).find(".product-brand").val().toUpperCase();

            if (targetRating === 0.0 || productRating >= targetRating) {
                if (brand==='ALL' || productBrand === brand) $(this).show();

            } else {
                $(this).hide();
            }
        });
    }

    // Event listener for brand change
    $("input[name='brand']").change(function () {
        const selectedBrand = $(this).val().toUpperCase();
        console.log("Selected Brand:", selectedBrand);
        brand=selectedBrand;
        filterByBrand(selectedBrand);
    });

    // Event listener for review rating change
    $("input[name='review']").change(function () {
        const selectedRating = parseFloat($(this).val());
        console.log("Selected Rating:", selectedRating);
        rate=selectedRating;
        filterByRating(selectedRating);
    });

});
