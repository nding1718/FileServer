package nding.springboot.FileServer.rest;

import nding.springboot.FileServer.domain.File;
import nding.springboot.FileServer.service.FileService;
import nding.springboot.FileServer.utils.MD5Tool;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("files", fileService.listFilesByPage(0, 20));
        return "index";
    }

    /**
     * Search for files in page format
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("files/{pageIndex}/{pageSize}")
    @ResponseBody
    public List<File> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return fileService.listFilesByPage(pageIndex, pageSize);
    }

    /**
     * Get file information
     *
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(@PathVariable String id) throws UnsupportedEncodingException {

        Optional<File> file = Optional.ofNullable(fileService.getFileById(id));

        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + new String(file.get().getName().getBytes("utf-8"),"ISO-8859-1"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .body(file.get().getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }

    /**
     * Show the files online
     *
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) {

        Optional<File> file = Optional.ofNullable(fileService.getFileById(id));

        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.get().getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.get().getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .body(file.get().getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }

    /**
     * Upload files
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    new Binary(file.getBytes()));
            f.setMd5(MD5Tool.getMD5(file.getInputStream()));
            fileService.saveFile(f);
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Your " + file.getOriginalFilename() + " is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    /**
     * interface used to upload the file
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        File returnFile = null;
        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    new Binary(file.getBytes()));
            f.setMd5(MD5Tool.getMD5(file.getInputStream()));
            returnFile = fileService.saveFile(f);
            String path = "//" + serverAddress + ":" + serverPort + "/view/" + returnFile.getId();
            return ResponseEntity.status(HttpStatus.OK).body(path);

        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    /**
     * Delete the file
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(@PathVariable String id) {

        try {
            fileService.removeFile(id);
            return ResponseEntity.status(HttpStatus.OK).body("DELETE Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
