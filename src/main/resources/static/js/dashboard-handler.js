// Wait for the DOM to be fully loaded

$(document).ready(function() {
    $(".sidebar a").on("click", function() {
        // Remove 'active' class from all buttons
        $(".sidebar a").removeClass("active");

        // Add 'active' class to the clicked button
        $(this).addClass("active");
    });
});


document.addEventListener("DOMContentLoaded", function () {

    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    // Select all sidebar buttons and containers
    const profileBtn = document.getElementById("profile-btn");
    const wishlistBtn = document.getElementById("wishlist-btn");
    const ordersBtn = document.getElementById("orders-btn");
    const logoutBtn = document.getElementById("logout-btn");

    const profileContainer = document.getElementById("profile-container");
    const wishlistContainer = document.getElementById("wishlist-container");
    const ordersContainer = document.getElementById("orders-container");

    const containers = [profileContainer, wishlistContainer, ordersContainer];

    // Function to show the selected container and hide others
    function showContainer(selectedContainer) {
        containers.forEach(container => {

            if (container === selectedContainer) {
                container.style.display = "block";
            } else {
                container.style.display = "none";
            }
        });
    }

    // Add event listeners to sidebar buttons
    profileBtn.addEventListener("click", function (e) {
        e.preventDefault();
        showContainer(profileContainer);
    });

    wishlistBtn.addEventListener("click", function (e) {
        e.preventDefault();
        showContainer(wishlistContainer);
    });

    ordersBtn.addEventListener("click", function (e) {
        e.preventDefault();
        showContainer(ordersContainer);
    });

    logoutBtn.addEventListener("click", function (e) {
        fetch('/logout', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', [csrfHeader]: csrfToken}
        }).then(response => {
            if (response.ok) {
                window.location.href = '/login'; // Redirect to login page after logout
            }
        }).catch(error => console.error('Error:', error));
    });



});
