
package kr.ac.jbnu.ksl.backendpractice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api")
public class BookController {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public record BookRequest(String title, String author) {}
    public record Book(
            Long id, String title, String author, boolean borrowed) {}

    // POST 1: 도서 한 권 등록
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<Book>> create(
            @RequestBody BookRequest request) {
        validate(request);
        Book book = new Book(
                sequence.incrementAndGet(),
                request.title(), request.author(), false);
        books.put(book.id(), book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(201, "도서 등록 성공", book));
    }

    // POST 2: 도서 여러 권 등록
    @PostMapping("/books/bulk")
    public ResponseEntity<ApiResponse<List<Book>>> createBulk(
            @RequestBody List<BookRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("등록할 도서가 없습니다.");
        }
        List<Book> result = requests.stream().map(request -> {
            validate(request);
            Book book = new Book(
                    sequence.incrementAndGet(),
                    request.title(), request.author(), false);
            books.put(book.id(), book);
            return book;
        }).toList();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(201, "도서 일괄 등록 성공", result));
    }

    // GET 1: 전체 도서 조회
    @GetMapping("/books")
    public ApiResponse<List<Book>> getAll() {
        return ApiResponse.of(200, "도서 목록 조회 성공",
                List.copyOf(books.values()));
    }

    // GET 2: 특정 도서 조회
    @GetMapping("/books/{id}")
    public ApiResponse<Book> getOne(@PathVariable Long id) {
        return ApiResponse.of(200, "도서 조회 성공", findBook(id));
    }

    // PUT 1: 도서 정보 수정
    @PutMapping("/books/{id}")
    public ApiResponse<Book> update(
            @PathVariable Long id,
            @RequestBody BookRequest request) {
        validate(request);
        Book old = findBook(id);
        Book updated = new Book(
                id, request.title(), request.author(), old.borrowed());
        books.put(id, updated);
        return ApiResponse.of(200, "도서 수정 성공", updated);
    }

    // PUT 2: 대출 상태 변경
    @PutMapping("/books/{id}/borrow")
    public ApiResponse<Book> borrow(@PathVariable Long id) {
        Book old = findBook(id);
        if (old.borrowed()) {
            throw new IllegalArgumentException("이미 대출된 도서입니다.");
        }
        Book updated = new Book(
                id, old.title(), old.author(), true);
        books.put(id, updated);
        return ApiResponse.of(200, "도서 대출 성공", updated);
    }

    // DELETE 1: 도서 삭제
    @DeleteMapping("/books/{id}")
    public ApiResponse<Book> delete(@PathVariable Long id) {
        Book removed = books.remove(id);
        if (removed == null) {
            throw new java.util.NoSuchElementException(
                    "도서를 찾을 수 없습니다. id=" + id);
        }
        return ApiResponse.of(200, "도서 삭제 성공", removed);
    }

    // DELETE 2: 대출 상태 해제
    @DeleteMapping("/books/{id}/borrow")
    public ApiResponse<Book> returnBook(@PathVariable Long id) {
        Book old = findBook(id);
        Book updated = new Book(
                id, old.title(), old.author(), false);
        books.put(id, updated);
        return ApiResponse.of(200, "도서 반납 성공", updated);
    }

    // 500 오류 테스트
    @GetMapping("/demo/error/500")
    public ResponseEntity<ApiResponse<Void>> error500() {
        return ResponseEntity.status(500)
                .body(ApiResponse.of(500, "내부 서버 오류 테스트", null));
    }

    // 503 오류 테스트
    @GetMapping("/demo/error/503")
    public ResponseEntity<ApiResponse<Void>> error503() {
        return ResponseEntity.status(503)
                .body(ApiResponse.of(503, "서비스 이용 불가 테스트", null));
    }

    private Book findBook(Long id) {
        Book book = books.get(id);
        if (book == null) {
            throw new java.util.NoSuchElementException(
                    "도서를 찾을 수 없습니다. id=" + id);
        }
        return book;
    }

    private void validate(BookRequest request) {
        if (request == null
                || request.title() == null
                || request.title().isBlank()
                || request.author() == null
                || request.author().isBlank()) {
            throw new IllegalArgumentException(
                    "도서 제목과 저자는 필수입니다.");
        }
    }
}
