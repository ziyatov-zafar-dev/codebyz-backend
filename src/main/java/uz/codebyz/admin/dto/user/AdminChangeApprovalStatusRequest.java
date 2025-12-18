package uz.codebyz.admin.dto.user;

import uz.codebyz.user.entity.ApprovalStatus;

public class AdminChangeApprovalStatusRequest {
    private ApprovalStatus status;
    private String approvalReason;

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }
}
