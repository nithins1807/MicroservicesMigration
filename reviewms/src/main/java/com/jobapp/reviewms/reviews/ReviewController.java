package com.jobapp.reviewms.reviews;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId){
        return new ResponseEntity(reviewService.getAllReviews(companyId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestParam Long companyId, @RequestBody Review review){
        boolean isReviewSaved = reviewService.createReview(companyId, review);
        if(isReviewSaved){
            return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review Not Found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId){
        return new ResponseEntity<>(reviewService.getReviewById(reviewId),HttpStatus.OK );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review){
        boolean isReviewUpdated = reviewService.updateReview(reviewId, review);
        if(isReviewUpdated){
            return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review cannot be updated", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){
        boolean isReviewDeleted = reviewService.deleteReview(reviewId);
        if(isReviewDeleted){
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review cannot be deleted", HttpStatus.NOT_FOUND);
    }
}
