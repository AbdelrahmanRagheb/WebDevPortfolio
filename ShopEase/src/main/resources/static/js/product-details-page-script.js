document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM fully loaded and parsed');
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    const productId = document.getElementById('productId').value;
    // Fetch comments on page load
    fetch(`/get-reviews?productId=${productId}`, {
        method: 'GET',
        headers: {[csrfHeader]: csrfToken}
    }).then(response => response.json()).then(reviews => {
        console.log('Comments fetched:', reviews);
        reviews.forEach(review => {
            const commentElement = document.createElement('div');
            commentElement.className = 'comment';
            commentElement.innerHTML = ` <div class="user">${review.username}</div> 
                                         <div class="time">${review.createdAt}</div> 
                                         <div class="text">${review.comment}</div> 
                                         <div class="ratingDisplay">${'★'.repeat(review.rating)}${'☆'.repeat(5 - review.rating)}</div>`;
            document.getElementById('commentsContainer').appendChild(commentElement);
        });
    }).catch(error => {
        console.error('Error fetching comments:', error);
    });
    document.getElementById('reviewForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const isLoggedIn = document.getElementById('is-auth').value === 'true'; ;

        console.log('User logged in:', isLoggedIn);
        if (!isLoggedIn) {
            console.log('User not logged in, showing alert');
            document.getElementById('custom-alert').style.display = 'block';
            return;
        }
        const username = document.getElementById('username').value ;

        const commentText = document.getElementById('commentInput').value;
        const rating = document.querySelector('input[name="rating"]:checked').value;

        console.log('Comment:', commentText);
        console.log('Rating:', rating);
        console.log('Product ID:', productId);
        if (!commentText || !rating || !productId) return;



        fetch('/add-review', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', [csrfHeader]: csrfToken},
            body: JSON.stringify({comment: commentText, rating: rating, productId: productId,username:username})
        }).then(response => response.json()).then(data => {
            console.log('Success:***********************************************************************', data);
            const commentElement = document.createElement('div');
            commentElement.className = 'comment';
            commentElement.innerHTML = ` <div class="user">${username}</div>
                                         <div class="time">${new Date().toLocaleString()}</div> 
                                         <div class="text">${commentText}</div> 
                                         <div class="ratingDisplay">${'★'.repeat(rating)}${'☆'.repeat(5 - rating)}</div> `;
            document.getElementById('commentsContainer').appendChild(commentElement);
            document.getElementById('commentInput').value = '';
            const checkedRadio = document.querySelector('input[name="rating"]:checked');
            if (checkedRadio) {
                checkedRadio.checked = false;
            }
        }).catch(error => {
            console.log('Success:***********************************************************************', data);
            console.error('Error:', error);
        });
    });

    document.getElementById('loginBtn').addEventListener('click', function () {
        window.location.href = '/login';

    });
    document.getElementById('signupBtn').addEventListener('click', function () {
        window.location.href = '/signup';

    });
});