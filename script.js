// Smooth scrolling for navigation links
document.addEventListener('DOMContentLoaded', function() {
    // Add smooth scrolling to all links with hash
    const links = document.querySelectorAll('a[href^="#"]');
    
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            const targetSection = document.querySelector(targetId);
            
            if (targetSection) {
                targetSection.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Add fade-in animation on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    // Observe all sections and cards
    const elementsToAnimate = document.querySelectorAll('.feature-card, .screenshot-item, .tech-item');
    
    elementsToAnimate.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });

    // Add click tracking for download buttons
    const downloadButtons = document.querySelectorAll('.download-buttons a');
    downloadButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Track download button clicks (you can integrate with analytics here)
            console.log('Download button clicked:', this.href);
        });
    });

    // Add hover effect for feature cards
    const featureCards = document.querySelectorAll('.feature-card');
    featureCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.borderColor = '#4ecdc4';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.borderColor = '#333';
        });
    });

    // Image lazy loading fallback for older browsers
    const images = document.querySelectorAll('img');
    images.forEach(img => {
        img.addEventListener('load', function() {
            this.style.opacity = '1';
        });
        
        img.addEventListener('error', function() {
            this.style.opacity = '0.5';
            console.log('Failed to load image:', this.src);
        });
    });

    // Add navigation highlight on scroll
    let lastScrollTop = 0;
    window.addEventListener('scroll', function() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        
        // Add scroll direction class for potential animations
        if (scrollTop > lastScrollTop) {
            document.body.classList.add('scrolling-down');
            document.body.classList.remove('scrolling-up');
        } else {
            document.body.classList.add('scrolling-up');
            document.body.classList.remove('scrolling-down');
        }
        
        lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
    });
});