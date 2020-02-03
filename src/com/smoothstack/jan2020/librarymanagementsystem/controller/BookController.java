package com.smoothstack.jan2020.librarymanagementsystem.controller;

import com.smoothstack.jan2020.librarymanagementsystem.Repository.*;
import com.smoothstack.jan2020.librarymanagementsystem.Repository.BookRepository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Author;
import com.smoothstack.jan2020.librarymanagementsystem.model.Book;
import com.smoothstack.jan2020.librarymanagementsystem.model.Book;
import com.smoothstack.jan2020.librarymanagementsystem.model.Publisher;
import com.smoothstack.jan2020.librarymanagementsystem.templates.Menu;
import com.smoothstack.jan2020.librarymanagementsystem.templates.MenuItem;

import java.util.*;
import java.util.function.Predicate;

public class BookController implements Controller {

    private BookRepository bookRepository = (BookRepository) Repository.getRepository(Book.class);
    private AuthorRepository authorRepository = (AuthorRepository) Repository.getRepository(Author.class);
    private PublisherRepository publisherRepository = (PublisherRepository) Repository.getRepository(Publisher.class);

    public String requestMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "bookMenu" :
                    return Objects.requireNonNull(bookMenu(model, requestParam));
                case "processBookMenu" :
                    return  Objects.requireNonNull(processBookMenu(model, requestParam));
                case "addBook" :
                    return Objects.requireNonNull(addBook(model, requestParam));
                case "updateBook" :
                    return Objects.requireNonNull(updateBook(model, requestParam));
                case "deleteBook" :
                    return Objects.requireNonNull(deleteBook(model, requestParam));
                case "readAllBook" :
                    return Objects.requireNonNull(readAllBook(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String bookMenu(Properties model, Properties requestParam) {
        Menu menu = new Menu();

        menu.setBanner("Book Services");
        menu.getMenuItemMap().put(1L, new MenuItem(menu, "Add Book"));
        menu.getMenuItemMap().put(2L, new MenuItem(menu, "Delete Book"));
        menu.getMenuItemMap().put(3L, new MenuItem(menu, "Update Book"));
        menu.getMenuItemMap().put(4L, new MenuItem(menu, "Read All Books"));
        menu.getMenuItemMap().put(5L, new MenuItem(menu, "Quit to Previous Menu"));


        Optional.ofNullable(requestParam.getProperty("error")).ifPresent(error->model.setProperty("error", error));
        Optional.ofNullable(requestParam.getProperty("info")).ifPresent(error->model.setProperty("info", error));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "Select > ");
        model.setProperty("callback", "processBookMenu");
        return "display_menu";
    }

    public String processBookMenu(Properties model, Properties requestParam) {

        switch(Objects.requireNonNull(requestParam.getProperty("choice"))) {
            case "1": model.setProperty("redirectEndPoint", "addBook");
                break;

            case "2": model.setProperty("redirectEndPoint", "deleteBook");
                break;

            case "3": model.setProperty("redirectEndPoint", "updateBook");
                break;

            case "4": model.setProperty("redirectEndPoint", "readAllBook");
                break;

            case "5": model.setProperty("redirectEndPoint", "mainMenu");
                break;

            default:
                model.setProperty("error", "Incorrect choice selected");
                model.setProperty("redirectEndPoint", "home");

        }

        return "redirect";
    }

    public String addBook(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> bookName = Optional.ofNullable(requestParam.getProperty("bookName"));
        Optional<String> authorId = Optional.ofNullable(requestParam.getProperty("authorId"));
        Optional<String> publisherId = Optional.ofNullable(requestParam.getProperty("publisherId"));

        if (!bookName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("prompt", "Book Name >");
                model.setProperty("callback", "addBook");
                return "string_input";
            } else {
                String name = input.get();
                Iterator<Book> books = bookRepository.find(book -> book.getName().toLowerCase().contains(name.toLowerCase()));
                StringBuilder sb = new StringBuilder();
                if (books.hasNext()) {
                    sb.append("List of all similar book(s)\n");
                    books.forEachRemaining(book -> sb.append("\t* ").append(book.getName()).append("\n"));
                    sb.append(String.format("\nNew Book '%s' \n", input.get()));

                } else {
                    sb.append( String.format("New Book '%s' \n", input.get()));
                }

                sb.append("\nAuthor List: \n");
                authorRepository.findAll().forEachRemaining(author ->
                        sb.append("\t").append(author.getLongId()).append(") ").append(author.getName()).append("\n"));

                model.setProperty("banner", sb.toString());
                model.setProperty("bookName", input.get());
                model.setProperty("prompt", "Author's Id > ");
                model.setProperty("callback", "addBook");
                return "string_input";
            }
        } else if (!authorId.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    Author author = authorRepository.get(id);

                    model.setProperty("authorId", input.get());
                    model.setProperty("bookName", bookName.get());

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Author's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Author id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("\nPublisher List: \n");
                publisherRepository.findAll().forEachRemaining(publisher ->
                        sb.append("\t").append(publisher.getLongId()).append(") ").append(publisher.getName()).append("\n"));
                model.setProperty("banner",sb.toString());
                model.setProperty("prompt", "Publisher's Id (0 = No publiher)> ");
                model.setProperty("callback", "addBook");
                return "string_input";
            }
        } else if (!publisherId.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    if (id != 0) {
                        Publisher publisher = publisherRepository.get(id);
                    }

                    model.setProperty("publisherId", input.get());
                    model.setProperty("authorId", authorId.get());
                    model.setProperty("bookName", bookName.get());

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Publisher's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Publisher id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }

                model.setProperty("prompt", "Save ? (y = save, n = abort) > ");
                model.setProperty("callback", "addBook");
                return "yesNo";
            }
        }

        else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {

                Author author = authorRepository.get(Long.parseLong(authorId.get()));
                Publisher publisher =
                        "0".equals(publisherId.get())?null:
                        publisherRepository.get(Long.parseLong(publisherId.get()));

                bookRepository.save(new Book(bookName.get(), author, publisher));

                model.setProperty("info", "Saved\n");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            }
        }

    }


    //TODO
    public String updateBook(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> bookId = Optional.ofNullable(requestParam.getProperty("bookId"));
        Optional<String> bookName = Optional.ofNullable(requestParam.getProperty("bookName"));
        Optional<String> authorId = Optional.ofNullable(requestParam.getProperty("authorId"));
        Optional<String> publisherId = Optional.ofNullable(requestParam.getProperty("publisherId"));

        if (!bookId.isPresent()) {

            if (!input.isPresent()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Modify Book, please enter Book's Id\n");
                sb.append("\nBook List: \n");
                bookRepository.findAll().forEachRemaining(book ->
                        sb.append("\t").append(book.getLongId()).append(") ").append(book.getName()).append("\n"));

                model.setProperty("banner", sb.toString());
                model.setProperty("prompt", "Book's Id > ");
                model.setProperty("callback", "updateBook");
                return "string_input";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    Book book = bookRepository.get(id);

                    model.setProperty("default", book.getName());
                    model.setProperty("prompt", "New Name >");
                    model.setProperty("bookId", input.get());
                    model.setProperty("callback", "updateBook");
                    return "string_input";

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Book's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Book id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }
            }
        } else if (!bookName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                String name = input.get();

                StringBuilder sb = new StringBuilder();

                Book book = bookRepository.get(Long.parseLong(bookId.get()));
                model.setProperty("default", String.valueOf(book.getAuthor().getLongId()));

                sb.append("\nAuthor List: \n");
                authorRepository.findAll().forEachRemaining(author ->
                        sb.append("\t").append(author.getLongId()).append(") ").append(author.getName()).append("\n"));

                model.setProperty("banner", sb.toString());
                model.setProperty("bookId", bookId.get());
                model.setProperty("bookName", input.get());
                model.setProperty("prompt", "Author's Id > ");
                model.setProperty("callback", "updateBook");
                return "string_input";
            }
        } else if (!authorId.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    Author author = authorRepository.get(id);

                    model.setProperty("authorId", input.get());
                    model.setProperty("bookId", bookId.get());
                    model.setProperty("bookName", bookName.get());

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Author's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Author id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }
                Book book = bookRepository.get(Long.parseLong(bookId.get()));
                model.setProperty("default", String.valueOf(book.getPublisher().getLongId()));

                StringBuilder sb = new StringBuilder();
                sb.append("\nPublisher List: \n");
                publisherRepository.findAll().forEachRemaining(publisher ->
                        sb.append("\t").append(publisher.getLongId()).append(") ").append(publisher.getName()).append("\n"));
                model.setProperty("banner",sb.toString());
                model.setProperty("prompt", "Publisher's Id (0 = No publiher)> ");
                model.setProperty("callback", "updateBook");
                return "string_input";
            }
        } else if (!publisherId.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    if (id != 0) {
                        Publisher publisher = publisherRepository.get(id);
                    }

                    model.setProperty("publisherId", input.get());
                    model.setProperty("authorId", authorId.get());
                    model.setProperty("bookId", bookId.get());
                    model.setProperty("bookName", bookName.get());

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Publisher's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Publisher id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }

                model.setProperty("prompt", "Save ? (y = save, n = abort) > ");
                model.setProperty("callback", "updateBook");
                return "yesNo";
            }
        }

        else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {

                Author author = authorRepository.get(Long.parseLong(authorId.get()));
                Publisher publisher =
                        "0".equals(publisherId.get())?null:
                                publisherRepository.get(Long.parseLong(publisherId.get()));

                Book book = bookRepository.get(Long.parseLong(bookId.get()));
                book.setName(bookName.get());
                book.setAuthor(author);
                book.setPublisher(publisher);
                bookRepository.save(book);

                model.setProperty("info", book.dump()+" Saved\n");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            }
        }

    }


    public String deleteBook(Properties model, Properties requestParam) {
        Optional<String> bookId = Optional.ofNullable(requestParam.getProperty("bookId"));
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));

        model.setProperty("banner", "Please enter Book's Id to DELETE!");
        model.setProperty("prompt", "Book's Id > ");
        model.setProperty("callback", "deleteBook");

        if (bookId.isPresent()) {

            // Confirmed
            if ("y".equalsIgnoreCase(input.orElse("n"))) {

                try {
                    long id = Long.parseLong(bookId.get());

                    Book book = bookRepository.delete(id);
                    model.setProperty("info", book.getName() + " was deleted!");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Book's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Book id not found in database.");
                    model.setProperty("redirectEndPoint", "bookMenu");
                    return "redirect";
                }
            } else {
                model.setProperty("error", "Operation was aborted by user");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            }

        }
        // Not Confirmed
        else if (input.isPresent()) {
            try {
                long id = Long.parseLong(input.get());
                Book book = bookRepository.get(id);

                model.setProperty("banner", String.format("Delete the following Book ?\n\n\tid = %d\n\tname = '%s'\n"
                        ,book.getLongId(),book.getName()));
                model.setProperty("prompt", "y = delete, n = abort > ");

                // preserve input as bookId
                model.setProperty("bookId", input.get());
                model.setProperty("callback", "deleteBook");
                return "yesNo";

            } catch (NumberFormatException e) {
                model.setProperty("error", "Please enter Book's Id as a number.");
                return "string_input";
            } catch (NoSuchElementException e) {
                model.setProperty("error", "Book id not found in database.");
                model.setProperty("redirectEndPoint", "bookMenu");
                return "redirect";
            }
        }


        return "string_input";
    }

    public String readAllBook(Properties model, Properties requestParam) {

        // Sort by name, get all Book
        Menu menu = getBookList(Comparator.comparing(Book::getName), captureAll -> true);

        model.setProperty("info", String.format("Total %d book(s) found.", menu.getMenuItemMap().size()));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "[ Enter To Continue ] ");
        model.setProperty("callback", "bookMenu");
        return "display_menu";
    }
    private Menu getBookList() {
        return getBookList(Comparator.comparingLong(Book::getLongId), captureAll -> true);
    }
    private Menu getBookList(Comparator<? super Book> comparator, Predicate<? super Book> filter) {
        Menu menu = new Menu();
        List<Book> allBook = new ArrayList<>();

        bookRepository.find(filter).forEachRemaining(allBook::add);

        if (comparator != null)
            allBook.sort(comparator);

        allBook.forEach(book-> menu.getMenuItemMap().put(book.getLongId(), new MenuItem(menu,
                String.format("%s, %s, %s",book.getName(), book.getAuthor().getName(), book.getPublisher()!=null?""+book.getPublisher().getName():"(no publisher)"))));

        return menu;
    }
}
