package com.autowash.autowashpro.service;

import com.autowash.autowashpro.dao.BookingDAO;
import com.autowash.autowashpro.dao.NotificationDAO;
import com.autowash.autowashpro.model.Booking;
import com.autowash.autowashpro.model.Notification;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * BookingService — Logic bổ trợ cho quy trình đặt lịch rửa xe.
 *
 * === LUỒNG DỮ LIỆU (DATA FLOW) ===
 * BookingWizardServlet gọi các method trong lớp này để:
 * 1. Tính danh sách ngày khả dụng dựa trên hạng thẻ → Wizard bước 2.
 * 2. Lấy danh sách slot và kiểm tra tình trạng → Wizard bước 3.
 * 3. Validate và tạo booking mới → Wizard bước 4.
 *
 * === TẠI SAO TÁCH SERVICE RA KHỎI SERVLET? ===
 * → Servlet chỉ lo nhận request/trả response.
 * → Service chứa logic tính toán ngày tháng, slot, validation.
 * → Dễ test và tái sử dụng logic.
 */
public class BookingService {

    private BookingDAO bookingDAO = new BookingDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Danh sách khung giờ cố định trong ngày
    // Có thể mở rộng hoặc lấy từ DB nếu cần linh hoạt hơn
    private static final String[] TIME_SLOTS = {
            "08:00-09:00",
            "09:00-10:00",
            "10:00-11:00",
            "11:00-12:00",
            "13:00-14:00",
            "14:00-15:00",
            "15:00-16:00",
            "16:00-17:00"
    };

    /**
     * Lấy danh sách ngày khả dụng cho booking dựa trên hạng thẻ.
     *
     * === LOGIC ===
     * - Bắt đầu từ NGÀY MAI (không cho đặt hôm nay).
     * - Số ngày tối đa = bookingDays (lấy từ Tier config):
     *   - MEMBER: 7 ngày
     *   - SILVER: 10 ngày
     *   - GOLD: 12 ngày
     *   - PLATINUM: 14 ngày
     * - Trả về List<Date> chứa các ngày từ mai đến hết giới hạn.
     *
     * @param bookingDays Số ngày tối đa được phép đặt trước (từ Tier.bookingDays)
     * @return Danh sách ngày khả dụng
     */
    public List<Date> getAvailableDates(int bookingDays) {
        List<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // Bắt đầu từ ngày mai
        cal.add(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < bookingDays; i++) {
            // Chuyển Calendar → java.sql.Date (chỉ lấy ngày, bỏ giờ)
            dates.add(new Date(cal.getTimeInMillis()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }

    /**
     * Lấy tình trạng các khung giờ trong một ngày cụ thể.
     *
     * === LOGIC ===
     * Với mỗi time slot cố định, đếm số booking đã đặt (không tính CANCELLED).
     * Nếu count >= MAX_SLOT_CAPACITY → slot bị "Hết chỗ" (disabled).
     *
     * @return Map<String, SlotInfo> với key = timeSlot string, value = thông tin slot
     */
    public Map<String, SlotInfo> getSlotAvailability(Date bookingDate) throws SQLException {
        Map<String, SlotInfo> slots = new LinkedHashMap<>(); // LinkedHashMap giữ thứ tự chèn

        for (String slot : TIME_SLOTS) {
            int bookedCount = bookingDAO.countByDateAndSlot(bookingDate, slot);
            boolean available = bookedCount < LoyaltyService.MAX_SLOT_CAPACITY;
            int remainingSpots = LoyaltyService.MAX_SLOT_CAPACITY - bookedCount;

            slots.put(slot, new SlotInfo(slot, available, bookedCount, remainingSpots));
        }

        return slots;
    }

    /**
     * Validate dữ liệu và tạo booking mới.
     *
     * @return true nếu tạo booking thành công
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public boolean validateAndCreateBooking(int userId, int plateId,
                                             Date bookingDate, String timeSlot, String notes)
            throws SQLException {

        // Validate 1: Ngày đặt phải từ ngày mai trở đi
        Date today = new Date(System.currentTimeMillis());
        if (!bookingDate.after(today)) {
            throw new IllegalArgumentException("Ngày đặt phải từ ngày mai trở đi.");
        }

        // Validate 2: Khung giờ phải hợp lệ
        boolean validSlot = false;
        for (String slot : TIME_SLOTS) {
            if (slot.equals(timeSlot)) {
                validSlot = true;
                break;
            }
        }
        if (!validSlot) {
            throw new IllegalArgumentException("Khung giờ không hợp lệ: " + timeSlot);
        }

        // Validate 3: Slot chưa hết chỗ
        int bookedCount = bookingDAO.countByDateAndSlot(bookingDate, timeSlot);
        if (bookedCount >= LoyaltyService.MAX_SLOT_CAPACITY) {
            throw new IllegalArgumentException("Khung giờ " + timeSlot + " đã hết chỗ.");
        }

        // Tạo booking
        Booking booking = new Booking(userId, plateId, bookingDate, timeSlot, notes);
        boolean created = bookingDAO.createBooking(booking);

        // Tạo thông báo đặt lịch thành công
        if (created) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
            Notification notif = new Notification(
                    userId,
                    "Đặt lịch thành công!",
                    String.format("Bạn đã đặt lịch rửa xe ngày %s, khung giờ %s. Hẹn gặp bạn tại AutoWash Pro!",
                            sdf.format(bookingDate), timeSlot)
            );
            notificationDAO.createNotification(notif);
        }

        return created;
    }

    /**
     * Lấy danh sách tên khung giờ (dùng cho JSP render).
     */
    public String[] getTimeSlots() {
        return TIME_SLOTS;
    }

    // ======================================================
    // INNER CLASS: Thông tin tình trạng một khung giờ
    // ======================================================

    /**
     * SlotInfo — Chứa thông tin trạng thái của một khung giờ.
     * Dùng để truyền xuống JSP hoặc chuyển thành JSON cho AJAX.
     */
    public static class SlotInfo {
        private String timeSlot;       // Tên khung giờ (VD: "09:00-10:00")
        private boolean available;     // true nếu còn chỗ
        private int bookedCount;       // Số lượng đã đặt
        private int remainingSpots;    // Số chỗ còn trống

        public SlotInfo(String timeSlot, boolean available, int bookedCount, int remainingSpots) {
            this.timeSlot = timeSlot;
            this.available = available;
            this.bookedCount = bookedCount;
            this.remainingSpots = remainingSpots;
        }

        public String getTimeSlot() { return timeSlot; }
        public boolean isAvailable() { return available; }
        public int getBookedCount() { return bookedCount; }
        public int getRemainingSpots() { return remainingSpots; }
    }
}
